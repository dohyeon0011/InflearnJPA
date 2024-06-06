package hellojpa.jpql;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue
    private Long id;

    private String name;

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
