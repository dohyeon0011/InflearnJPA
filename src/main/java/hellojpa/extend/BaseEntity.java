package hellojpa.extend;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

// 중복되는 컬럼(속성)만을 갖고 받고 싶을 때 (상속 개념이 아님)
// 이 컬럼들을 상속 받을 클래스에 extends 해주고, 추상 클래스에 @MappedSuperclass 해주면 됨
@MappedSuperclass
public abstract class BaseEntity { // 직접 사용할 일이 없으므로 추상 클래스 권장

    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
