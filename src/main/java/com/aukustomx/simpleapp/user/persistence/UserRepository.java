package com.aukustomx.simpleapp.user.persistence;

import com.aukustomx.simpleapp.user.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public User byId(int id) {
        return em.find(User.class, id);
    }
}
