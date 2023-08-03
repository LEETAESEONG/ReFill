package com.refill.reservation.dto.response;

import com.refill.reservation.entity.Reservation;
import java.time.LocalDateTime;

public record ReservationListResponse(
    Long reservationId,
    String hospitalName,
    String doctorName,
    LocalDateTime startDateTime,
    boolean isCanceled
) {

    public ReservationListResponse(Reservation reservation) {
        this (
            reservation.getId(),
            reservation.getDoctor().getHospital().getName(),
            reservation.getDoctor().getName(),
            reservation.getStartDateTime(),
            reservation.isCanceled()
        );
    }


}
