/**
 * input validation (kantaan ei saa mennä kuin valvottuja arvoja)
 * 
 * päätös on tehty
 * suunnitelma on hyväksytty
 * suunnitelma on muutettu
 * 
 * 
 * 
 * http://www.springboottutorial.com/unit-testing-for-spring-boot-rest-services
 * 
 * 
 */

package fi.espoo.pythia.backend.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.espoo.pythia.backend.mgrs.StorageManager;
import fi.espoo.pythia.backend.repos.entities.Project;
import fi.espoo.pythia.backend.transfer.PlanValue;
import fi.espoo.pythia.backend.transfer.ProjectValue;

@RestController
@RequestMapping("/pythia/v1")
public class StorageRestController {

	@Autowired
	private StorageManager storageManager;

	// -------------------------GET-------------------------------

	/**
	 * return all projects
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/projects/", produces = "application/json")
	public ResponseEntity<List<ProjectValue>> getProject() {

		try {
			List<ProjectValue> project = storageManager.getProjects();
			return new ResponseEntity<List<ProjectValue>>(project, HttpStatus.OK);
		} catch (java.lang.NullPointerException e) {
			return new ResponseEntity<List<ProjectValue>>(HttpStatus.NOT_FOUND);
		}catch (org.springframework.transaction.CannotCreateTransactionException e){
			return new ResponseEntity<List<ProjectValue>>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * return a single project by id if found. Otherwise return null.
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/projects/{projectId}", produces = "application/json")
	public ResponseEntity<ProjectValue> getProject(@PathVariable("projectId") Long projectId) {

		try {
			ProjectValue project = storageManager.getProject(projectId);
			return new ResponseEntity<ProjectValue>(project, HttpStatus.OK);
		} catch (java.lang.NullPointerException e) {
			return new ResponseEntity<ProjectValue>(new ProjectValue(), HttpStatus.NOT_FOUND);
		}catch (org.springframework.transaction.CannotCreateTransactionException e){
			return new ResponseEntity<ProjectValue>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * return a single project by hansuprojectid if found. Otherwise return null.
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/projects/hansuprojectid/{hansuProjectId}", produces = "application/json")
	public ResponseEntity<ProjectValue> getHansuProject(@PathVariable("hansuProjectId") String hansuId) {

		try {
			ProjectValue project = storageManager.getProjectByHansuId(hansuId);
			return new ResponseEntity<ProjectValue>(project, HttpStatus.OK);
		} catch (java.lang.NullPointerException e) {
			return new ResponseEntity<ProjectValue>(new ProjectValue(), HttpStatus.NOT_FOUND);
		}catch (org.springframework.transaction.CannotCreateTransactionException e){
			return new ResponseEntity<ProjectValue>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * 
	 * return all plans by projectId
	 * 
	 * @param projectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/projects/{projectId}/plans/", produces = "application/json")
	public ResponseEntity<List<PlanValue>> getPlan(@PathVariable("projectId") Long projectId) {

		try {
			List<PlanValue> plan = storageManager.getPlans(projectId);
			return new ResponseEntity<List<PlanValue>>(plan, HttpStatus.OK);
		} catch (java.lang.NullPointerException e) {
			return new ResponseEntity<List<PlanValue>>(HttpStatus.NOT_FOUND);
		}catch (org.springframework.transaction.CannotCreateTransactionException e){
			return new ResponseEntity<List<PlanValue>>(HttpStatus.FORBIDDEN);
		}

	}

	// --------------------------POST-------------------------------------

	/**
	 * create a new plan to the db and return the whole project with all attributes
	 * 
	 * @param projectValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/projects/{projectId}/plans/", produces = "application/json", consumes = "application/json")
	public ResponseEntity<PlanValue> createPlan(@RequestBody PlanValue planV) {

		// catch exception database connection
		//
		// Value object mapping
		try {
			PlanValue savedPlan = storageManager.createPlan(planV);
			return new ResponseEntity<PlanValue>(savedPlan, HttpStatus.OK);
		} catch (org.springframework.transaction.CannotCreateTransactionException e) {
			return new ResponseEntity<PlanValue>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * create a new project to the db and return the whole project with all
	 * attributes
	 * 
	 * @param projectValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/projects/", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ProjectValue> createProject(@RequestBody ProjectValue project) {

		// Value object mapping
		try {
			ProjectValue savedProject = storageManager.createProject(project);
			return new ResponseEntity<ProjectValue>(savedProject, HttpStatus.OK);
		} catch (org.springframework.transaction.CannotCreateTransactionException e) {
			return new ResponseEntity<ProjectValue>(HttpStatus.FORBIDDEN);
		}

	}

	// ---------------------------PUT--------------------------------

	/**
	 * update a plan of a project
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/projects/{projectId}/plans/{planId}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<PlanValue> updatePlan(@RequestBody PlanValue planV) {

		try {
			PlanValue updatedPlan = storageManager.updatePlan(planV);
			return new ResponseEntity<PlanValue>(updatedPlan, HttpStatus.OK);
		} catch (org.springframework.transaction.CannotCreateTransactionException e) {
			return new ResponseEntity<PlanValue>(HttpStatus.FORBIDDEN);
		}

	}

	

	/**
	 * update a project
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value = "/projects/{projectId}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<ProjectValue> updateProject(@RequestBody ProjectValue projectV) {

		try {
			ProjectValue updatedProject = storageManager.updateProject(projectV);
			return new ResponseEntity<ProjectValue>(updatedProject, HttpStatus.OK);
		} catch (org.springframework.transaction.CannotCreateTransactionException e) {
			return new ResponseEntity<ProjectValue>(HttpStatus.FORBIDDEN);
		}

	}
	
	// ------------------------ NOT DONE --------------------------


	@DeleteMapping("/projects/{projectId}")
	public void deleteProject() {

	}
}
