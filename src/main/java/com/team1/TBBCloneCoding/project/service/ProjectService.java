package com.team1.TBBCloneCoding.project.service;

import com.team1.TBBCloneCoding.common.dto.ResponseDto;
import com.team1.TBBCloneCoding.member.entity.Member;
import com.team1.TBBCloneCoding.project.dto.ProjectCreateRequestDto;
import com.team1.TBBCloneCoding.project.entity.Image;
import com.team1.TBBCloneCoding.project.entity.Project;
import com.team1.TBBCloneCoding.project.mapper.ProjectMapper;
import com.team1.TBBCloneCoding.project.repository.ImageReposirory;
import com.team1.TBBCloneCoding.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ImageReposirory imageReposirory;
    private final ProjectMapper projectMapper;

    @Transactional
    public ResponseDto createProject(ProjectCreateRequestDto projectCreateRequestDto, Member member) {
        Project project = projectMapper.toEntity(projectCreateRequestDto, member);
        projectRepository.save(project);

        Image image;
        List<Long> thumbnailListNumber = projectCreateRequestDto.getThumbnailList();
        for(Long i : thumbnailListNumber){
            image = imageReposirory.findById(i).orElseThrow(
                    () -> new NullPointerException("id에 맞는 이미지가 썸네일이미지 데이터베이스에 존재하지 않습니다.")
            );
            image.thumbnailImageConnectionWithProject(project);
        }

        List<Long> imageNumberList = projectCreateRequestDto.getContentImageList();
        for(Long i : imageNumberList){
            // 저장된 이미지를 레포지토리 가져와서 연결
            image = imageReposirory.findById(i).orElseThrow(
                    () -> new NullPointerException("id에 맞는 이미지가 콘텐트이미지 데이터베이스에 존재하지 않습니다.")
            );
            image.contentImageConnectionWithProject(project);
        }

        return new ResponseDto("success","프로젝트 등록에 성공하셨습니다.",null);
    }
    @Transactional
    public ResponseDto createSupport(Member member, Long projectId, SupportCreateRequestDto supportCreateRequestDto){

        Long memberId = member.getMemberId();

        Member memberForCreateSupport = memberRepository.findById(memberId).orElseThrow(
            () -> new NullPointerException()
        );

        Project project = projectRepository.findById(projectId).orElseThrow(
            () -> new NullPointerException()
        );

        Support support = supportMapper.toSupport(memberForCreateSupport, project, supportCreateRequestDto);

        supportRepository.save(support);

        return new ResponseDto("success","후원 성공", null);
    }

    public ResponseDto createProjectLike(Member member, Long projectId){
    @Transactional

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시물 입니다.")
        );

        Optional<ProjectLike> findProjectLike = projectLikeRepository.findByProjectAndMember(project, member);

        if(findProjectLike.isPresent()){
            projectLikeRepository.deleteByProjectAndMember(project, member);
            return new ResponseDto("success","좋아요 취소 성공", null);
        }

        ProjectLike projectLike = projectLikeMapper.toProjectLike(member, project);
        projectLikeRepository.save(projectLike);

        return new ResponseDto("success","좋아요 등록 성공", null);
    }

}