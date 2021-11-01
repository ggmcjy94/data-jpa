package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity { // 순수 jpa

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


    @PrePersist
    public void prePersist() {
        LocalDateTime now  = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; // 등록 할때 수정일 도 넣어놓는게 편하다 널값보단 있는게 나음
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
