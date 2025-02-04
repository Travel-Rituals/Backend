package travel.travel.location.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.image.domain.Image;
import travel.travel.image.dto.ImageResDto;
import travel.travel.image.repository.ImageRepository;
import travel.travel.image.service.ImageService;
import travel.travel.location.domain.Location;
import travel.travel.location.dto.LocationCreateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.dto.LocationUpdateReqDto;
import travel.travel.location.repository.LocationRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    private final LocationRepository locationRepository;
    private final PlanRepository planRepository;
    private final ImageService imageService;

    public LocationResDto LocationCreate(@Valid LocationCreateReqDto locationCreateReqDto, MultipartFile file) throws IOException {
        Plan plan = planRepository.findById(locationCreateReqDto.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 계획입니다."));
        ImageResDto imageResDto = imageService.uploadFile(file);
        Image image =  Image.builder()
                .imageId(imageResDto.getImageId())
                .imageUrl(imageResDto.getImageUrl())
                .build();

        Location location = locationRepository.findTopByPlanAndDayOrderByScheduleOrderDesc(plan, locationCreateReqDto.getDay());
        Integer lastOrderNumber = 0;
        if (location != null) {
            lastOrderNumber = location.getScheduleOrder();
        }
        Integer newOrderNumber = lastOrderNumber + 1;

        Location savedLocation = locationRepository.save(locationCreateReqDto.toEntity(plan, image, newOrderNumber));
        return savedLocation.fromEntity();
    }

    public List<LocationResDto> LocationReadList(Long planId) {
        Plan plan =  planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 계획입니다."));
        List<LocationResDto> locations = locationRepository.findByPlan(plan).stream()
                .map(Location::fromEntity)
                .collect(Collectors.toList());

        return locations;
    }

    public List<LocationResDto> LocationReadDayList(Long planId, LocalDate day) {
        Plan plan =  planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 계획입니다."));
        List<LocationResDto> locations = locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(plan, day).stream()
                .map(Location::fromEntity)
                .collect(Collectors.toList());
        return locations;
    }

    public List<LocationResDto> LocationUpdate(@Valid List<LocationUpdateReqDto> locationUpdateReqDtos, Long planId, LocalDate day) {
        Plan plan =  planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 계획입니다."));
        List<LocationResDto> dtos = new ArrayList<>();

        for (LocationUpdateReqDto locationUpdateReqDto : locationUpdateReqDtos) {
            Location existingLocation =  locationRepository.findById(locationUpdateReqDto.getLocationId()).orElseThrow();
            if (!existingLocation.getPlan().equals(plan) || !existingLocation.getDay().equals(day)) {
                throw new IllegalArgumentException();
            }
            existingLocation.updateLocation(locationUpdateReqDto.toEntity());
            Location savedLocation = locationRepository.save(existingLocation);
            dtos.add(savedLocation.fromEntity());
        }

        checkForDuplicateScheduleOrder(dtos);
        List<Location> locations = locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(plan, day);
        autoScheduleOrder(locations);

        dtos = locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(plan, day).stream()
                .map(Location::fromEntity)
                .collect(Collectors.toList());

        return dtos;
    }

    public LocationResDto locationDelete(Long locationId) {
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 위치입니다."));
        locationRepository.delete(existingLocation);
        Plan plan =  planRepository.findById(existingLocation.getPlan().getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않은 계획입니다."));

        List<Location> locations = locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(plan, existingLocation.getDay());
        autoScheduleOrder(locations);

        return existingLocation.fromEntity();
    }

    private void checkForDuplicateScheduleOrder(List<LocationResDto> dtos) {
        List<Integer> orderList = dtos.stream()
                .map(LocationResDto::getScheduleOrder)
                .collect(Collectors.toList());

        long distinctCount = orderList.stream().distinct().count();
        if (distinctCount != orderList.size()) {
            throw new IllegalArgumentException("중복된 scheduleOrder 값이 있습니다. 순서를 다시 확인해주세요.");
        }
    }

    private void autoScheduleOrder(List<Location> locations) {
        Integer newOrderNumber = 1;
        for (Location location : locations) {
            location.updateLocation(Location.builder().scheduleOrder(newOrderNumber++).build());
            locationRepository.save(location);
        }
    }
}