package com.team1.TBBCloneCoding.project.service;

import com.team1.TBBCloneCoding.common.dto.ResponseDto;
import com.team1.TBBCloneCoding.member.entity.Member;
import com.team1.TBBCloneCoding.project.dto.ProjectCreateRequestDto;
import com.team1.TBBCloneCoding.project.dto.ProjectUpdateRequestDto;
import com.team1.TBBCloneCoding.project.entity.Image;
import com.team1.TBBCloneCoding.project.dto.ProjectListResponseDto;
import com.team1.TBBCloneCoding.project.entity.Project;
import com.team1.TBBCloneCoding.project.entity.Support;
import com.team1.TBBCloneCoding.project.mapper.ProjectMapper;
import com.team1.TBBCloneCoding.project.repository.ImageReposirory;
import com.team1.TBBCloneCoding.project.repository.LikeRepository;
import com.team1.TBBCloneCoding.project.repository.ProjectRepository;
import com.team1.TBBCloneCoding.project.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ImageReposirory imageReposirory;
    private final ProjectMapper projectMapper;
    private final SupportRepository supportRepository;
    private final LikeRepository likeRepository;
    @Transactional(readOnly = true)
    public ResponseDto getProjectList(String filter, String category) {

        List<Project> projectList;
        // filter에 따라서 정렬순서변경
        if(filter.equals("oldest")){
            // 오래된순
            projectList = projectRepository.findAllByCategoryOrderByCreatedAtAsc(category);
        }
        else if(filter.equals("popular")){
            // 인기순
           projectList = projectRepository.findAllByCategoryOrderByRecommendCountDesc(category);
        }
        // 기본값 : 최신순 filter(latest)
        projectList = projectRepository.findAllByCategoryOrderByCreatedAtDesc(category);

        // projectList에서 project를 뽑아서 projectListResponseDto로 변환해서 전달
        ProjectListResponseDto projectListResponseDto;
        List<Support> supportList;
        List<ProjectListResponseDto> projectListResponseDtoList = new ArrayList<>();
        for(Project project : projectList){
            // totalSupport, percent 변수선언, goalPrice 불러오기
            Long totalSupport = 0L;
            Long goalPrice = project.getGoalPrice();
            Double percent = 0.0;
        }
        //project.update(projectUpdateRequestDto);
        project = projectMapper.projectUpdateRequestDtoToEntity(projectUpdateRequestDto);

        return new ResponseDto("success","프로젝트 수정에 성공했습니다.",null);
        
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


    @Transactional(readOnly = true)
    public ResponseDto getProjectList(String filter, String category) {

        List<Project> projectList;
        // filter에 따라서 정렬순서변경
        if(filter.equals("oldest")){
            // 오래된순
            projectList = projectRepository.findAllByCategoryOrderByCreatedAtAsc(category);
        }
        else if(filter.equals("popular")){
            // 인기순
           projectList = projectRepository.findAllByCategoryOrderByRecommendCountDesc(category);
        }
        // 기본값 : 최신순 filter(latest)
        projectList = projectRepository.findAllByCategoryOrderByCreatedAtDesc(category);

        // projectList에서 project를 뽑아서 projectListResponseDto로 변환해서 전달
        ProjectListResponseDto projectListResponseDto;
        List<Support> supportList;
        List<ProjectListResponseDto> projectListResponseDtoList = new ArrayList<>();
        for(Project project : projectList){

            // totalSupport, percent 변수선언, goalPrice 불러오기
            Long totalSupport = 0L;
            Long goalPrice = project.getGoalPrice();
            Double percent = 0.0;

            // totalSupport 구하기
            supportList = supportRepository.findAllByProject(project);
            for(Support support : supportList){
                totalSupport = totalSupport + support.getSupportAmount();
            }

            // percent = totalSupport/goalPrice
            percent = Double.valueOf(totalSupport / goalPrice);

            // percent 소숫점 자르기
            percent = Math.floor(percent);
            Long longPercent = percent.longValue();

            // 좋아요 갯수 반환
            int projectLike = likeRepository.findAllByProject(project).size();

            projectListResponseDto = projectMapper.entityToProjectListResponseDto(project,totalSupport,longPercent,projectLike);
            projectListResponseDtoList.add(projectListResponseDto);
        }

        return new ResponseDto("success", "프로젝트 리스트 조회에 성공했습니다.", projectListResponseDtoList);
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