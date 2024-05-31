package hellojpa;

import jakarta.persistence.*;

@Entity
public class Locker {

    @Id @GeneratedValue
    @Column(name = "LOCKER_ID")
    private long id;

    private String name;

    // 일대일 양방향
    @OneToOne(mappedBy = "locker")
    private Member member;


}
