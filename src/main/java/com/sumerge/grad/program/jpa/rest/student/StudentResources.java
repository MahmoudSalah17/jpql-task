package com.sumerge.grad.program.jpa.rest.student;

import com.sumerge.grad.program.jpa.entity.Address;
import com.sumerge.grad.program.jpa.entity.Course;
import com.sumerge.grad.program.jpa.entity.Student;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.sumerge.grad.program.jpa.constants.Constants.PERSISTENT_UNIT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("/students")
public class StudentResources {

    @PersistenceContext(unitName = PERSISTENT_UNIT)
    private EntityManager em;


    @Context
    HttpServletRequest request;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response getAllStudents() {

        try {
            return Response.ok().
                    entity(em.createQuery("SELECT s FROM Student s", Student.class).getResultList()).
                    build();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e).
                    build();
        }
    }

    @Transactional
    @PUT
    public Response addStudent(Student student)
    {
        try{
            if(student.getId() != null)
                throw new IllegalArgumentException("Can't create student since it exists in the database");
            em.merge(student);
            return Response.ok().entity(student).build();
        }
        catch (Exception e)
        {
            return Response.serverError().
                    entity(e).
                    build();
        }
    }



    @GET
    @Path("/total_hours/{id}")
    public Response getTotalHours(@PathParam("id") Long id)
    {
       try {
           return Response.ok().entity(em
                   .createQuery("SELECT SUM(c.hours) FROM Course c INNER JOIN c.students s WHERE s.id = :sId")
                   .setParameter("sId", id).getSingleResult()).build();
       }
       catch (Exception e)
       {
           return Response.serverError().
                   entity(e).
                   build();
       }
    }

    @GET
    @Path("/course/{name}")
    public Response getStudentsByCourse(@PathParam("name") String courseName)
    {
        try {
            return Response.ok().entity(em.createQuery("SELECT s FROM Student s JOIN s.courses c WHERE c.name = :cName").setParameter("cName", courseName).getResultList()).build();
        }
        catch (Exception e)
        {
            return Response.serverError().
                    entity(e).
                    build();
        }
    }

    @GET
    @Path("/city/{city}")
    public Response getStudentByCity(@PathParam("city") String cityName)
    {
        try {
            return Response.ok().entity(
                    em.createQuery("SELECT s FROM Student s INNER JOIN s.addresses a WHERE a.city = :cName").setParameter("cName", cityName).getResultList()).build();
        }
        catch (Exception e)
        {
            return Response.serverError().
                    entity(e).
                    build();
        }
    }

    @GET
    @Path("/city_count/{city}")
    public Response countStudentByCity(@PathParam("city") String cityName)
    {
        try {
            return Response.ok().entity(
                    em.createQuery("SELECT COUNT(s) FROM Student s INNER JOIN s.addresses a WHERE a.city = :cName").setParameter("cName", cityName).getResultList()).build();
        }
        catch (Exception e)
        {
            return Response.serverError().
                    entity(e).
                    build();
        }
    }

//    @Transactional
//    @PUT
//    public Response modifyAddress(Address address)
//    {
//
//        try {
//            if (address.getId() == null)
//                throw new IllegalArgumentException("Can't create student since it exists in the database");
//
//            em.merge(address);
//            return Response.ok().entity(address).
//                    build();
////            Address merged =  em.merge(address);
////            return Response.ok().entity(merged).build();
//        }
//        catch (Exception e)
//        {
//            LOGGER.log(SEVERE, e.getMessage(), e);
//            return Response.serverError().
//                    entity(e).
//                    build();
//        }
//    }

//    @PUT
//    @Transactional
////    @Path("{id}")
//    public Response addAddress(Address address)
//    {
//        try{
//            if(address.getId() != null)
//                throw new IllegalArgumentException("Can't create student since it exists in the database");
////            Student student = em.find(Student.class, id);
////            student.addAddress(address);
////            em.merge(student);
////            return Response.ok().entity(student).build();
//
//        }
//        catch (Exception e)
//        {
//            return Response.serverError().
//                    entity(e).
//                    build();
//        }
//    }

    @GET
    @Path("/search")
    public Response searchStudents(@QueryParam("name") String name, @QueryParam("city") String city,
                                   @QueryParam("country") String country, @QueryParam("course_name") String courseName)
    {
        try {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> studentRoot = criteriaQuery.from(Student.class);
            criteriaQuery.select(studentRoot);
            List<Predicate> predicates = new ArrayList<>();
            if(name != null) {
                predicates.add(criteriaBuilder.equal(studentRoot.<String>get("name"), name));
            }
            if(city != null || country !=null)
            {
                Join<Student, Address> addressJoin = studentRoot.join("addresses", JoinType.INNER);
                if(city != null)
                    predicates.add(criteriaBuilder.equal(addressJoin.<String>get("city"), city));
                if(country != null)
                    predicates.add(criteriaBuilder.equal(addressJoin.<String>get("country"), country));
            }
            if(courseName != null)
            {
                Join<Student, Course> courseJoin = studentRoot.join("courses", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(courseJoin.<String>get("name"), courseName));
            }
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<Student> query = em.createQuery(criteriaQuery);
            return Response.ok().entity(query.getResultList()).build();
        }
        catch (Exception e)
        {
            return Response.serverError().
                    entity(e).
                    build();
        }


    }

}
