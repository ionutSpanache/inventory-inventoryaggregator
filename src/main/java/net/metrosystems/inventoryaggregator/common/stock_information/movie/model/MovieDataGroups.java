package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class MovieDataGroups {

    String dataGroup;
    Integer nextOpenOrders;
    List<String> reasons;
    String bookingDate;

    public MovieDataGroups(String dataGroup, Integer nextOpenOrders) {
        this.dataGroup = dataGroup;
        this.nextOpenOrders = nextOpenOrders;
    }

    public MovieDataGroups(String dataGroup, List<String> reasons) {
        this.dataGroup = dataGroup;
        this.reasons = reasons;
    }

    public MovieDataGroups(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    public MovieDataGroups(String dataGroup, LocalDate bookingDate) {
        this.dataGroup = dataGroup;
        setBookingDate(bookingDate);
    }

    public void setBookingDate(String bookingDate) {
        throw new NotImplementedException("Please pass bookingDate as LocalDate object");
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
