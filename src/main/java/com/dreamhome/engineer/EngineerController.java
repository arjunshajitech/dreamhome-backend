package com.dreamhome.engineer;

import com.dreamhome.config.Constants;
import com.dreamhome.config.CookieHelper;
import com.dreamhome.config.PasswordEncoder;
import com.dreamhome.exception.CustomBadRequestException;
import com.dreamhome.exception.CustomUnauthorizedException;
import com.dreamhome.repository.ProjectImageRepository;
import com.dreamhome.repository.ProjectRepository;
import com.dreamhome.repository.UserRepository;
import com.dreamhome.request.*;
import com.dreamhome.table.Project;
import com.dreamhome.table.ProjectImage;
import com.dreamhome.table.Users;
import com.dreamhome.table.enumeration.ApproveReject;
import com.dreamhome.table.enumeration.ImageType;
import com.dreamhome.table.enumeration.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_ENGINEER)
public class EngineerController {

    final UserRepository userRepository;
    final CookieHelper cookieHelper;
    final ProjectRepository projectRepository;
    final ProjectImageRepository projectImageRepository;


    @PostMapping("/signup")
    public Success signup(@RequestBody EngineerSignup engineerSignup) throws CustomBadRequestException {

        boolean isEmailExists = userRepository.existsByEmailAndRole(engineerSignup.getEmail(), Role.ENGINEER);
        boolean isPhoneExists = userRepository.existsByPhoneAndRole(engineerSignup.getPhone(),Role.ENGINEER);
        if(isEmailExists || isPhoneExists) throw new CustomBadRequestException("Email or phone exists.");

        userRepository.save(
                new Users(engineerSignup.getRole(),false,ApproveReject.PENDING,engineerSignup.getYearOfExperience(),
                        engineerSignup.getJobTitle(),engineerSignup.getPhone(), PasswordEncoder.encodePassword(engineerSignup.getPassword()),
                        engineerSignup.getEmail(),engineerSignup.getName())
        );
        return new Success("Signup successful");
    }

    @PostMapping("/login")
    public Success login(@RequestBody Login login, HttpServletResponse response) throws CustomBadRequestException {

        Users user = userRepository.findByEmailAndRole(login.getEmail(), Role.ENGINEER);
        if (user == null || user.getStatus() != ApproveReject.APPROVED) throw new CustomBadRequestException("Bad Credentials.");
        if (!PasswordEncoder.isPasswordMatch(login.getPassword(),user.getPassword())) throw new CustomBadRequestException("Bad Credentials.");

        cookieHelper.setCookie(response,user,Constants.ENGINEER_COOKIE_NAME);
        return new Success("Successfully logged in.");
    }

    @GetMapping("/logout")
    public Success logout(HttpServletRequest request, HttpServletResponse response) {
        cookieHelper.deleteCookie(request,response,Constants.ENGINEER_COOKIE_NAME);
        return new Success("Successfully logged out.");
    }

    @GetMapping("/profile")
    public Users getProfile(HttpServletRequest request) throws CustomUnauthorizedException {
        return sessionCheck(request);
    }

    @GetMapping("/jobs")
    public List<Project> listAllAssignedWorks(HttpServletRequest request) throws  CustomUnauthorizedException {
        Users user = sessionCheck(request);
        return projectRepository.findAllByEngineerId(user.getId());
    }

    @PutMapping("/plan/project")
    public Success updatePlan(@RequestBody UpdateProject updateProject,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndEngineerId(updateProject.getId(),user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        project.setPlanEstimation(updateProject.getPlanEstimation());
        project.setPlanAmount(updateProject.getPlanAmount());
        project.setPlanEstimationSubmitted(true);
        projectRepository.save(project);
        return new Success("Plan updated successfully.");
    }

    @PutMapping("/model/project")
    public Success updateModel(@RequestBody UpdateProject updateProject,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndEngineerId(updateProject.getId(),user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        project.setThreeDModelAmount(updateProject.getThreeDModelAmount());
        project.setThreeDModelEstimation(updateProject.getThreeDModelEstimation());
        project.setThreeDModelEstimationSubmitted(true);
        projectRepository.save(project);
        return new Success("Model updated successfully.");
    }

    @PostMapping("/upload/plan")
    public Success uploadPlan(@RequestBody UploadImage uploadPlan, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndEngineerId(uploadPlan.getProjectId(),user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        if (!project.isPlanAmountPaid())
            throw new CustomBadRequestException("Plan amount not paid.");

        projectImageRepository.save(new ProjectImage(
                user.getId(),project.getClientId(),project.getId(),uploadPlan.getImageId(), ImageType.PLAN
        ));
        return new Success("Upload plan successful.");
    }

    @PostMapping("/upload/model")
    public Success uploadModel(@RequestBody UploadImage uploadPlan, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndEngineerId(uploadPlan.getProjectId(),user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        if (!project.isThreeDModelAmountPaid())
            throw new CustomBadRequestException("Three DModel amount is not paid.");

        projectImageRepository.save(new ProjectImage(
                user.getId(),project.getClientId(),project.getId(),uploadPlan.getImageId(), ImageType.MODEL
        ));
        return new Success("Upload plan successful.");
    }

    @GetMapping("/plan/images/{projectId}")
    public List<ProjectImage> getProjectPlanImages(@PathVariable("projectId") UUID projectId,
                                                   HttpServletRequest request) throws CustomBadRequestException, CustomUnauthorizedException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");
        return projectImageRepository.findAllByProjectIdAndEngineerIdAndType(projectId,user.getId(), ImageType.PLAN);
    }

    @GetMapping("/model/images/{projectId}")
    public List<ProjectImage> getProjectModelImages(@PathVariable("projectId") UUID projectId,
                                                    HttpServletRequest request) throws CustomBadRequestException, CustomUnauthorizedException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");
        return projectImageRepository.findAllByProjectIdAndEngineerIdAndType(projectId,user.getId(), ImageType.MODEL);
    }

    private Users sessionCheck(HttpServletRequest request) throws CustomUnauthorizedException {
        String cookieId = cookieHelper.getCookieValue(request,Constants.ENGINEER_COOKIE_NAME);
        Users users = userRepository.findByCookie(UUID.fromString(cookieId));
        if (users != null && users.getStatus() == ApproveReject.APPROVED) return users;
        else throw new CustomUnauthorizedException("Unauthorized.");
    }
}
