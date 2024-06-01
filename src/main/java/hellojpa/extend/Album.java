package hellojpa.extend;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A") // DB의 DTYPE에 어떤 값으로 들어가게 할 지 정하고 싶을 때, 자식 테이블에서 설정
public class Album extends Item {

    private String artist;

}
