package RateControl.Models.User;

import RateControl.Models.Org.Org;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints=@UniqueConstraint(columnNames = {"email"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String email;

    private String password;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="org_id")//, nullable=false)
    private Org org;

    public User(String email, String password, Org org) {
        this.email = email;
        this.password = password;
        this.org = org;
    }

    public User() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public UUID getOrgId() {
        return org.getId();
    }
}
