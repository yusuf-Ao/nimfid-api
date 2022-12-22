package com.nimfid.modelservice.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Scope("singleton")
@AllArgsConstructor
@Slf4j
public class CoordinateConverter {

    private final ModelConfigs modelConfigs;

    public LatLng convertToLatLng(final String address, final String city, final String lga, final String state) {
        try {
            GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(modelConfigs.getApiKey())
                .build();
            GeocodingResult[] results =  GeocodingApi.geocode(context,
                    address+", "+city+", "+lga+", "+state+"state, Nigeria.").await();
            context.shutdown();
            return results[0].geometry.location;
        } catch (IOException | InterruptedException | ApiException e) {
            log.error("Error occurred while converting address to coordinates", e);
            return new LatLng(9.0820,8.6753);
        }

    }
}
