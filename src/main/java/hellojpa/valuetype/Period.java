package hellojpa.valuetype;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable // 값 타입이라고 알려줌
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Period() {
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
