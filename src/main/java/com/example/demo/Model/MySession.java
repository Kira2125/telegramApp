package com.example.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MySession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String tag;
    @Column
    private byte[] session_id;
    @Column
    private long salt;
    @Column
    private int contentRelatedCount;
    @Column
    private long lastMessageId;
    @Column
    private String markerName;
    @Column
    private String dataCenterIp;
    @Column
    private int dataCenterPort;
    @Column
    private String phoneNumber;

}
