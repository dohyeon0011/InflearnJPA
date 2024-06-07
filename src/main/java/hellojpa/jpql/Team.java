package hellojpa.jpql;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;

    private String name;

    // select * from t from t 라 했을 때 이 쿼리 결과 값(team a 정보, team b 정보)을 내부에 결과 100개를 미리 깔아둠(N+1문제 잡힘)
//    @BatchSize(size = 100) // persistence.xml에서 글로벌 설정 가능
    @OneToMany(mappedBy = "team")
    private List<JpqlMember> members = new ArrayList<>();

    public void addMember(JpqlMember member) {
        this.members.add(member);
        member.setTeam(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JpqlMember> getMembers() {
        return members;
    }

    public void setMembers(List<JpqlMember> members) {
        this.members = members;
    }
}
