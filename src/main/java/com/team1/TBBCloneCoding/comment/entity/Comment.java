package com.team1.TBBCloneCoding.comment.entity;

import com.team1.TBBCloneCoding.common.entity.TimeStamp;
import com.team1.TBBCloneCoding.member.entity.Member;
import com.team1.TBBCloneCoding.project.entity.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "Project_Id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "Member_Id", nullable = false)
    private Member member;

    @Builder
    public Comment(String contents, Project project, Member member) {
        this.contents = contents;
        this.project = project;
        this.member = member;
    }

    public void updateComment(String contents){
        this.contents = contents;
    }
    
}
