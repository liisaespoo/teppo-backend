package fi.espoo.pythia.backend.mgrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.espoo.pythia.backend.mappers.LPToLPValueMapper;
import fi.espoo.pythia.backend.mappers.PlanToPlanValueMapper;
import fi.espoo.pythia.backend.mappers.PlanValueToPlanMapper;
import fi.espoo.pythia.backend.mappers.PrjToPrjVal2Mapper;
import fi.espoo.pythia.backend.mappers.PrjUpToPrjUpValMapper;
import fi.espoo.pythia.backend.mappers.PrjUpValToPrjUpMapper;
import fi.espoo.pythia.backend.mappers.PtextToPtextValueMapper;
import fi.espoo.pythia.backend.mappers.PtextValueToPtextMapper;
import fi.espoo.pythia.backend.repos.LatestPlansRepository;
import fi.espoo.pythia.backend.repos.PlanRepository;
import fi.espoo.pythia.backend.repos.ProjectRepository;
import fi.espoo.pythia.backend.repos.ProjectUpdateRepository;
import fi.espoo.pythia.backend.repos.PtextRepository;
import fi.espoo.pythia.backend.repos.SisterProjectRepository;
import fi.espoo.pythia.backend.repos.SisterProjectUpdateRepository;
import fi.espoo.pythia.backend.repos.entities.LatestPlans;
import fi.espoo.pythia.backend.repos.entities.Plan;
import fi.espoo.pythia.backend.repos.entities.Project;
import fi.espoo.pythia.backend.repos.entities.ProjectUpdate;
import fi.espoo.pythia.backend.repos.entities.Ptext;
import fi.espoo.pythia.backend.repos.entities.SisterProject;
import fi.espoo.pythia.backend.repos.entities.SisterProjectUpdate;
import fi.espoo.pythia.backend.transfer.LatestPlansValue;
import fi.espoo.pythia.backend.transfer.PlanValue;
import fi.espoo.pythia.backend.transfer.ProjectUpdateValue;
import fi.espoo.pythia.backend.transfer.ProjectValue2;
import fi.espoo.pythia.backend.transfer.PtextValue;

@Component
@Transactional
public class StorageManager {

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private SisterProjectRepository sisterProjectRepository;

	@Autowired
	private SisterProjectUpdateRepository sisterProjectUpdateRepository;

	@Autowired
	private PtextRepository ptextRepository;

	@Autowired
	private ProjectUpdateRepository projectUpdateRepository;
	
	@Autowired
	private LatestPlansRepository latestPlansRepository;
	// ---------------------GET------------------------------------

	/**
	 * NEW
	 * 
	 * @return list of projects
	 */
	public ArrayList<ProjectValue2> getProjects2() {

		ArrayList<Project> prjList = (ArrayList<Project>) projectRepository.findAll();
		ArrayList<ProjectValue2> prjValList = new ArrayList();

		// for -loop for prjList

		for (Project p : prjList) {
			// map each project to projectValue
			ProjectValue2 pval = PrjToPrjVal2Mapper.ProjectToProjectValue2(p);
			prjValList.add(pval);
		}
		// return projectValue -ArrayList
		return prjValList;
	}

	/**
	 * Return project object for given id from database. If project is not found
	 * for id, returns null. DONE
	 */
	public ProjectValue2 getProject2(Long projectId) {

		Project project = projectRepository.findByProjectId(projectId);
		ProjectValue2 pval = PrjToPrjVal2Mapper.ProjectToProjectValue2(project);
		return pval;

	}

	/**
	 * 
	 * @param hansuId
	 * @return
	 */
	public ProjectValue2 getProjectByHansuId2(String hansuId) {
		List<Project> prjList = projectRepository.findAll();
		for (Project p : prjList) {
			if (p.getHansuProjectId().equals(hansuId)) {
				ProjectValue2 pval = PrjToPrjVal2Mapper.ProjectToProjectValue2(p);
				return pval;
			}
		}

		return null;
	}

	/**
	 * get all plans by projectId
	 * 
	 * @param projectId
	 * @return
	 */
	public List<PlanValue> getPlans(Long projectId) {

		ProjectUpdate projectUpdate = projectUpdateRepository.findByProjectId(projectId);
		ProjectUpdateValue pval = PrjUpToPrjUpValMapper.ProjectUpdateToProjectUpdateValue(projectUpdate);

		return pval.getPlans();

	}

	public PlanValue getPlan(Long planId) {

		Plan plan = planRepository.findByPlanId(planId);
		ProjectUpdate project = plan.getProject();
		PlanValue pVal = PlanToPlanValueMapper.planToPlanValue(plan, project);

		return pVal;
	}

	public PtextValue getComment(long id) {
		Ptext comment = ptextRepository.findByTextId(id);
		Plan plan = comment.getPlan();
		PtextValue cVal = PtextToPtextValueMapper.ptextToPtextValue(comment, plan);

		return cVal;
	}

	/**
	 * get comments by planId
	 * 
	 * @param planId
	 * @return
	 */
	public List<PtextValue> getComments(Long planId) {

		Plan plan = planRepository.findByPlanId(planId);
		List<Ptext> comments =ptextRepository.findByPlan(plan);

		List<PtextValue> commentValues = new ArrayList<PtextValue>();
		for (Ptext c : comments) {
			PtextValue cv = PtextToPtextValueMapper.ptextToPtextValue(c, plan);
			commentValues.add(cv);
		}

		return commentValues;

	}
	
//	/**
//	 * get comments 
//	 * @param planId
//	 * @return
//	 */
//	public List<Comment> getComments(Long planId) {
//
//		//Plan plan = planRepository.findByPlanId(planId);
//		List<Comment> comments = commentRepository.findAll();
//
//		System.out.println("Comment list size:"+comments.size());
////		List<CommentValue> commentValues = new ArrayList<CommentValue>();
////		for (Comment c : comments) {
////			System.out.println("Comment c:"+c.getText());
////			CommentValue cv = CommentToCommentValueMapper.commentToCommentValue(c, plan);
////			commentValues.add(cv);
////		}
//
//		// TODO Auto-generated method stub
//		return comments;
//
//	}

	// ---------------------POST-----------------------------------

	public ProjectUpdate createProject(ProjectUpdateValue projectV) {

		ProjectUpdate projectUpTemp = projectUpdateRepository.findByProjectId(projectV.getProjectId());
		ProjectUpdate projectUp = PrjUpValToPrjUpMapper.projectValue2ToProject(projectV, projectUpTemp, false);
		ProjectUpdate updatedProject = projectUpdateRepository.save(projectUp);
		updateSisterProjects(projectV, projectUp);

		return updatedProject;
	}

	/**
	 * Checks if 1st version and if approved
	 * 
	 * If the 1st then version = 0 and approved = true
	 * 
	 * If not the 1st then increase version number by one
	 * 
	 * @return PlanValue
	 */
	public PlanValue createPlan(PlanValue planV) {

		Long projectId = planV.getProjectId();
		// get project by projectid
		ProjectUpdate projectUpdate = projectUpdateRepository.findByProjectId(projectId);
		// map planV to plan
		Plan mappedPlan = PlanValueToPlanMapper.planValueToPlan(planV, projectUpdate, false);

		// get all plans with planV.projectId and planV.mainNo & planV.subNo

		List<Plan> existingPlans = planRepository.findByProjectInAndMainNoInAndSubNoIn(projectUpdate, planV.getMainNo(),
				planV.getSubNo());

		short version = 0;
		boolean approved = true;
		// first version
		if (existingPlans.isEmpty()) {
			version = 0;
			approved = true;

		} else {
			// sorting from min to max
			Collections.sort(existingPlans);
			// get existingPlans max version with projctId, mainno and subno
			Plan max = existingPlans.get(existingPlans.size() - 1);
			// max version
			short maxVersion = max.getVersion();
			version = (short) (maxVersion + 1);
			approved = false;

		}
		mappedPlan.setVersion(version);
		mappedPlan.setApproved(approved);

		Plan savedPlan = planRepository.save(mappedPlan);

		PlanValue savedPlanValue = PlanToPlanValueMapper.planToPlanValue(savedPlan, projectUpdate);
		// finally
		return savedPlanValue;
	}

	/**
	 * 
	 * @param commV
	 * @param id
	 * @return
	 */
	public PtextValue createComment(PtextValue commV, Long id) {

		// Long planId = commV.getPlanId();
		System.out.println("ID:"+id);
		Plan plan = planRepository.findByPlanId(id);
		System.out.println("Plan id:" + plan.getPlanId());

		Ptext comm = PtextValueToPtextMapper.commentValueToComment(commV, plan, false);
		System.out.println("Comm plan id:" + comm.getPlan().getPlanId());
		
	    Ptext savedComm = ptextRepository.save(comm);

		System.out.println("savedComm id:" + savedComm.getTextId());
	    
		PtextValue savedCommValue = PtextToPtextValueMapper.ptextToPtextValue(savedComm, plan);
		return savedCommValue;

	}

	// ---------------------PUT------------------------------------

	// /**
	// *
	// * @param projectV
	// * @return
	// */
	// public ProjectValue updateProject(ProjectValue projectV) {
	//
	// Project projectTemp =
	// projectRepository.findByProjectId(projectV.getProjectId());
	// Project project =
	// ProjectValueToProjectMapper.projectValueToProjectUpdate(projectV,
	// projectTemp);
	// Project updatedProject = projectRepository.save(project);
	//
	// ProjectValue updatedProjectValue =
	// ProjectToProjectValueMapper.projectToProjectValue(updatedProject);
	// return updatedProjectValue;
	//
	// }

	public void updateProject(ProjectUpdateValue projectV) {

		ProjectUpdate projectUpTemp = projectUpdateRepository.findByProjectId(projectV.getProjectId());
		ProjectUpdate projectUp = PrjUpValToPrjUpMapper.projectValue2ToProject(projectV, projectUpTemp, true);
		// update basic project table
		projectUpdateRepository.save(projectUp);

		// update sisterProjects table
		updateSisterProjects(projectV, projectUp);

		// ProjectValue2 updatedProjectValue2 = PrjToPrjVal2
		// .ProjectToProjectValue2(projectRepository.findByProjectId(projectV.getProjectId()));
		// return updatedProjectValue2;
	}

	public void updateSisterProjects(ProjectUpdateValue pv, ProjectUpdate projectUp) {

		// get latest by repo id
		ProjectUpdate p = projectUpdateRepository.findByProjectId(pv.getProjectId());
		// create empty sisterprojects list
		List<SisterProject> sProjects = new ArrayList<SisterProject>();

		// first delete all rows with this project
		sisterProjectUpdateRepository.deleteByProject(projectUp);
		Long j = 1L;
		// add sisterprojects to the db
		for (Long id : pv.getSisterProjects()) {
			System.out.println("updatesisterProjectId:" + id);
			SisterProjectUpdate sProjectUp = (new SisterProjectUpdate(j, projectUp, id));

			sisterProjectUpdateRepository.save(sProjectUp);
			j++;
		}
	}

	/**
	 * 
	 * @param planV
	 * @return
	 */

	public LatestPlansValue updatePlan(PlanValue planV) {

		Long id = planV.getProjectId();
		ProjectUpdate projectUp = projectUpdateRepository.findByProjectId(id);
		Plan plan = PlanValueToPlanMapper.planValueToPlan(planV, projectUp, true);

		Plan updatedPlan = planRepository.save(plan);
		
		LatestPlans latestPlans = latestPlansRepository.findByPlanId(updatedPlan.getPlanId());
		Project project = projectRepository.findByProjectId(id);

		LatestPlansValue updatedLAtestPlanValue = LPToLPValueMapper.lpTolpValue(latestPlans, project);
		return updatedLAtestPlanValue;

	}

	public PtextValue updateComment(PtextValue commV) {

		Long id = commV.getPlanId();
		Plan plan = planRepository.findByPlanId(id);
		Ptext comm = PtextValueToPtextMapper.commentValueToComment(commV, plan, true);

		Ptext updatedComm = ptextRepository.save(comm);

		PtextValue updatedCommentValue = PtextToPtextValueMapper.ptextToPtextValue(updatedComm, plan);
		return updatedCommentValue;

	}

}
