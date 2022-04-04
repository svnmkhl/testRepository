package main;

import javax.persistence.*;

@Entity
@Table(name = "page")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path", columnDefinition = "TEXT")
    private String path;

    private int code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;


    public Page () {

    }

    public Page (String path, int code, String content) {
        this.path = path;
        this.code = code;
        this.content = content;
    }
    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
