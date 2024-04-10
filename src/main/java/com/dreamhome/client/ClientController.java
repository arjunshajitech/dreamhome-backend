package com.dreamhome.client;


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
import com.dreamhome.table.enumeration.ProjectStatus;
import com.dreamhome.table.enumeration.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constants.API_V1_CLIENT)
public class ClientController {

    final UserRepository userRepository;
    final CookieHelper cookieHelper;
    final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;

    @PostMapping("/signup")
    public Success signup(@RequestBody ClientSignup clientSignup, HttpServletRequest request,
                          HttpServletResponse response) throws CustomBadRequestException {

        boolean isEmailExists = userRepository.existsByEmailAndRole(clientSignup.getEmail(),Role.CLIENT);
        boolean isPhoneExists = userRepository.existsByPhoneAndRole(clientSignup.getPhone(),Role.CLIENT);
        if(!isEmailExists || !isPhoneExists) throw new CustomBadRequestException("Email or phone exists.");

        userRepository.save(
                new Users(clientSignup.getRole(),false,ApproveReject.PENDING,0,
                        null,clientSignup.getPhone(),PasswordEncoder.encodePassword(clientSignup.getPassword()),
                        clientSignup.getEmail(),clientSignup.getName())
        );
        return new Success("Signup successful");
    }

    @PostMapping("/login")
    public Success login(@RequestBody Login login, HttpServletResponse response) throws CustomBadRequestException {

        Users user = userRepository.findByEmailAndRole(login.getEmail(), Role.CLIENT);
        if (user == null || user.getStatus() != ApproveReject.APPROVED) throw new CustomBadRequestException("Bad Credentials.");
        if (!PasswordEncoder.isPasswordMatch(login.getPassword(),user.getPassword())) throw new CustomBadRequestException("Bad Credentials.");

        cookieHelper.setCookie(response,user,Constants.CLIENT_COOKIE_NAME);
        return new Success("Successfully logged in.");
    }

    @PostMapping("/logout")
    public Success logout(HttpServletRequest request, HttpServletResponse response) {
        cookieHelper.deleteCookie(request,response,Constants.CLIENT_COOKIE_NAME);
        return new Success("Successfully logged out.");
    }

    @GetMapping("/profile")
    public Users getProfile(HttpServletRequest request) throws CustomUnauthorizedException {
        return sessionCheck(request);
    }

    @PostMapping ("/project")
    public Success createProject(@RequestBody CreateProject createProject,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        boolean isProjectExists = projectRepository.existsByNameAndClientId(createProject.getName(),user.getId());
        if (isProjectExists) throw new CustomBadRequestException("Project already exists.");

        projectRepository.save(new Project(
                createProject.getName(),createProject.getType(),createProject.getArchitectureStyle(),
                createProject.getTimeline(),createProject.getDescription()
        ));
        return new Success("Project created.");
    }

    @GetMapping("/project")
    public List<Project> getAllProjects(HttpServletRequest request) throws CustomUnauthorizedException {
        Users user = sessionCheck(request);
        return projectRepository.findAllByClientId(user.getId());
    }

    @PutMapping ("/project/{id}")
    public Success updateProject(@PathVariable("id") UUID projectId,
                                 @RequestBody CreateProject createProject,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        if (project.getStatus().equals(ProjectStatus.ASSIGNED))
            throw new CustomBadRequestException("Cannot update assigned project.");

        project.setName(createProject.getName());
        project.setType(createProject.getType());
        project.setDescription(createProject.getDescription());
        project.setArchitectureStyle(createProject.getArchitectureStyle());
        project.setTimeline(createProject.getTimeline());
        projectRepository.save(project);
        return new Success("Project updated.");
    }

    @DeleteMapping("/project/{id}")
    public Success deleteProject(@PathVariable("id") UUID projectId,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        if (project.getStatus().equals(ProjectStatus.ASSIGNED))
            throw new CustomBadRequestException("Cannot delete assigned project.");

        projectRepository.delete(project);
        return new Success("Project deleted.");
    }

    @PostMapping("/pay/model/{projectId}")
    public Success payModel(@PathVariable("projectId") UUID id,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(id,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        project.setThreeDModelAmountPaid(true);
        projectRepository.save(project);
        return new Success("Payment successful.");
    }

    @PostMapping("/pay/plan/{projectId}")
    public Success payPlan(@PathVariable("projectId") UUID id,HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(id,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");

        project.setPlanAmountPaid(true);
        projectRepository.save(project);
        return new Success("Payment successful.");
    }

    @GetMapping("/plan/images/{projectId}")
    public List<ProjectImage> getProjectPlanImages(@PathVariable("projectId") UUID projectId,
                                                   HttpServletRequest request) throws CustomBadRequestException, CustomUnauthorizedException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");
        return projectImageRepository.findAllByProjectIdAndClientIdAndType(projectId,user.getId(), ImageType.PLAN);
    }

    @GetMapping("/model/images/{projectId}")
    public List<ProjectImage> getProjectModelImages(@PathVariable("projectId") UUID projectId,
                                                   HttpServletRequest request) throws CustomBadRequestException, CustomUnauthorizedException {

        Users user = sessionCheck(request);
        Project project = projectRepository.findByIdAndClientId(projectId,user.getId());
        if (project == null) throw new CustomBadRequestException("Project not found.");
        return projectImageRepository.findAllByProjectIdAndClientIdAndType(projectId,user.getId(), ImageType.MODEL);
    }

    @PostMapping("/plan/approve-reject")
    public Success approveRejectPlan(@RequestBody ImageApproveReject imageApproveReject, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        ProjectImage image = projectImageRepository.findByIdAndClientIdAndType(imageApproveReject.getId(),user.getId(),ImageType.PLAN);
        if (image == null) throw new CustomBadRequestException("Project image not found.");

        image.setApproved(imageApproveReject.isApprove());
        image.setReason(imageApproveReject.getReason());
        projectImageRepository.save(image);
        return new Success("Approved/Rejected plan.");
    }

    @PostMapping("/model/approve-reject")
    public Success approveRejectModel(@RequestBody ImageApproveReject imageApproveReject, HttpServletRequest request)
            throws CustomUnauthorizedException, CustomBadRequestException {

        Users user = sessionCheck(request);
        ProjectImage image = projectImageRepository.findByIdAndClientIdAndType(imageApproveReject.getId(),user.getId(),ImageType.MODEL);
        if (image == null) throw new CustomBadRequestException("Project image not found.");

        image.setApproved(imageApproveReject.isApprove());
        image.setReason(imageApproveReject.getReason());
        projectImageRepository.save(image);
        return new Success("Approved/Rejected plan.");
    }

    private Users sessionCheck(HttpServletRequest request) throws CustomUnauthorizedException {
        String cookieId = cookieHelper.getCookieValue(request,Constants.CLIENT_COOKIE_NAME);
        Users users = userRepository.findByCookie(UUID.fromString(cookieId));
        if (users != null && users.getStatus() != ApproveReject.APPROVED) return users;
        else throw new CustomUnauthorizedException("Unauthorized.");
    }
}
