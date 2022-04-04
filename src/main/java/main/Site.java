package main;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Status status;

    @Column (name = "status_time")
    private Date statusTime;

    @Column (name = "last_error")
    private String lastError;

    private String url;

    private String name;

    public Site(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
