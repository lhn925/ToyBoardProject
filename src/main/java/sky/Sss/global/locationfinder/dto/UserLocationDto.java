package sky.Sss.global.locationfinder.dto;


import com.maxmind.geoip2.model.CityResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.user.model.UserAgent;

@Getter
@Slf4j
public class UserLocationDto {

    private String ipAddress;

    private String city;

    private String countryName;
    // 위도
    private String latitude;

    // 경도
    private String longitude;

    @Builder
    public UserLocationDto(String ipAddress, String city, String countryName, String latitude, String longitude) {
        this.ipAddress = ipAddress;
        this.city = city;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UserLocationDto create(CityResponse response) {
        String countryName = response.getCountry().getIsoCode();
        String cityName = response.getCity().getName();
        String ipAddress = response.getTraits().getIpAddress();
        String latitude = String.valueOf(response.getLocation().getLatitude());
        String longitude = String.valueOf(response.getLocation().getLongitude());
        return UserLocationDto.builder()
            .city(cityName)
            .countryName(countryName)
            .ipAddress(ipAddress)
            .latitude(latitude)
            .longitude(longitude).build();
    }



}