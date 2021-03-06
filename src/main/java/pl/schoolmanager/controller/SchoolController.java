package pl.schoolmanager.controller;

import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.schoolmanager.bean.SessionManager;
import pl.schoolmanager.entity.Division;
import pl.schoolmanager.entity.School;
import pl.schoolmanager.entity.Student;
import pl.schoolmanager.entity.Subject;
import pl.schoolmanager.entity.Teacher;
import pl.schoolmanager.repository.DivisionRepository;
import pl.schoolmanager.repository.MessageRepository;
import pl.schoolmanager.repository.SchoolRepository;
import pl.schoolmanager.repository.StudentRepository;
import pl.schoolmanager.repository.SubjectRepository;
import pl.schoolmanager.repository.TeacherRepository;

@Controller
@RequestMapping("/school")
public class SchoolController {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private DivisionRepository divisionRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SessionManager sessionManager;
	

	@GetMapping("/all")
	public String all(Model m) {
		m.addAttribute("schools", this.schoolRepository.findAll());
		return "school/all_schools";
	}

	@GetMapping("/create")
	public String createSchool(Model m) {
		m.addAttribute("school", new School());
		return "school/new_school";
	}

	@PostMapping("/create")
	public String createSchoolPost(@Valid @ModelAttribute School school, BindingResult bindingResult, Model m) {
		if (bindingResult.hasErrors()) {
			return "school/new_school";
		}
		this.schoolRepository.save(school);
		return "index";
	}

	@GetMapping("/view/{schoolId}")
	public String viewSchool(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		m.addAttribute("school", school);
		return "school/show_school";
	}

	@GetMapping("/update/{schoolId}")
	public String updateSchool(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		m.addAttribute("school", school);
		return "school/edit_school";
	}

	@PostMapping("/update/{schoolId}")
	public String updateSchoolPost(@Valid @ModelAttribute School school, BindingResult bindingResult,
			@PathVariable long schoolId) {
		if (bindingResult.hasErrors()) {
			return "school/edit_school";
		}
		school.setId(schoolId);
		this.schoolRepository.save(school);
		return "index";
	}

	@GetMapping("/delete/{schoolId}")
	public String deleteSchool(@PathVariable long schoolId, Model m) {
		m.addAttribute("schools", this.schoolRepository.findAll());
		m.addAttribute("remove", schoolId);
		return "school/all_schools";
	}

	@PostMapping("/delete/{schoolId}")
	public String deleteSchool(@PathVariable long schoolId) {	
		try {
			this.schoolRepository.delete(schoolId);
		} catch (ConstraintViolationException | PersistenceException | JpaSystemException e) {
			return "errors/deleteException";
		}
		return "redirect:/school/all";
	}

	@GetMapping("/addDivision/{schoolId}")
	public String addDivision(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		List<Division> schoolDivisions = this.divisionRepository.findAllBySchoolId(schoolId);
		List<Division> freeDivisions = this.divisionRepository.findAllBySchoolIdIsNull();
		m.addAttribute("school", school);
		m.addAttribute("schoolDivisions", schoolDivisions);
		m.addAttribute("freeDivisions", freeDivisions);
		return "school/addDivision_school";
	}

	@GetMapping("addDivision/{schoolId}/{divisionId}")
	public String addDivision(@PathVariable long schoolId, @PathVariable long divisionId) {
		School school = this.schoolRepository.findOne(schoolId);
		Division division = this.divisionRepository.findOne(divisionId);
		division.setSchool(school);
		this.divisionRepository.save(division);
		return "redirect:/school/addDivision/{schoolId}";
	}

	@GetMapping("removeDivision/{schoolId}/{divisionId}")
	public String removeDivision(@PathVariable long schoolId, @PathVariable long divisionId) {
		Division division = this.divisionRepository.findOne(divisionId);
		division.setSchool(null);
		this.divisionRepository.save(division);
		return "redirect:/school/addDivision/{schoolId}";
	}

	@GetMapping("/addSubject/{schoolId}")
	public String addSubject(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		List<Subject> schoolSubjects = this.subjectRepository.findAllBySchoolId(schoolId);
		List<Subject> freeSubjects = this.subjectRepository.findAllBySchoolIdIsNull();
		m.addAttribute("school", school);
		m.addAttribute("schoolSubjects", schoolSubjects);
		m.addAttribute("freeSubjects", freeSubjects);
		return "school/addSubject_school";
	}

	@GetMapping("addSubject/{schoolId}/{subjectId}")
	public String addSubject(@PathVariable long schoolId, @PathVariable long subjectId) {
		School school = this.schoolRepository.findOne(schoolId);
		Subject subject = this.subjectRepository.findOne(subjectId);
		subject.setSchool(school);
		this.subjectRepository.save(subject);
		return "redirect:/school/addSubject/{schoolId}";
	}

	@GetMapping("removeSubject/{schoolId}/{subjectId}")
	public String removeSubject(@PathVariable long schoolId, @PathVariable long subjectId) {
		Subject subject = this.subjectRepository.findOne(subjectId);
		subject.setSchool(null);
		this.subjectRepository.save(subject);
		return "redirect:/school/addSubject/{schoolId}";
	}

	@GetMapping("/addStudent/{schoolId}")
	public String addStudent(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		Set<Student> schoolStudents = this.studentRepository.findAllBySchoolId(schoolId);
		Set<Student> notSchoolStudents = this.studentRepository.findAllBySchoolIdIsNullOrSchoolIdIsNot(schoolId);
		m.addAttribute("school", school);
		m.addAttribute("schoolStudents", schoolStudents);
		m.addAttribute("notSchoolStudents", notSchoolStudents);
		return "school/addStudent_school";
	}

	@GetMapping("addStudent/{schoolId}/{studentId}")
	public String addStudent(@PathVariable long schoolId, @PathVariable long studentId) {
		School school = this.schoolRepository.findOne(schoolId);
		Student student = this.studentRepository.findOne(studentId);
		// student.getSchool().add(school);
		student.setSchool(school);
		this.studentRepository.save(student);
		return "redirect:/school/addStudent/{schoolId}";
	}

	@GetMapping("removeStudent/{schoolId}/{studentId}")
	public String removeStudent(@PathVariable long schoolId, @PathVariable long studentId) {
		Student student = this.studentRepository.findOne(studentId);
		this.studentRepository.save(student);
		return "redirect:/school/addStudent/{schoolId}";
	}

	@GetMapping("/addTeacher/{schoolId}")
	public String addTeacher(Model m, @PathVariable long schoolId) {
		School school = this.schoolRepository.findOne(schoolId);
		Set<Teacher> schoolTeachers = this.teacherRepository.findAllBySchoolId(schoolId);
		Set<Teacher> notSchoolTeachers = this.teacherRepository.findAllBySchoolIdIsNullOrSchoolIdIsNot(schoolId);
		m.addAttribute("school", school);
		m.addAttribute("schoolTeachers", schoolTeachers);
		m.addAttribute("notSchoolTeachers", notSchoolTeachers);
		return "school/addTeacher_school";
	}

	@GetMapping("addTeacher/{schoolId}/{teacherId}")
	public String addTeacher(@PathVariable long schoolId, @PathVariable long teacherId) {
		School school = this.schoolRepository.findOne(schoolId);
		Teacher teacher = this.teacherRepository.findOne(teacherId);
		teacher.setSchool(school);
		this.teacherRepository.save(teacher);
		return "redirect:/school/addTeacher/{schoolId}";
	}

	@GetMapping("removeTeacher/{schoolId}/{teacherId}")
	public String removeTeacher(@PathVariable long schoolId, @PathVariable long teacherId) {
		Teacher teacher = this.teacherRepository.findOne(teacherId);
		teacher.setSchool(null);
		this.teacherRepository.save(teacher);
		return "redirect:/school/addTeacher/{schoolId}";
	}

	@ModelAttribute("availableSchools")
	public List<School> getSchools() {
		return this.schoolRepository.findAll();
	}
}
