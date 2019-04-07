package com.aukustomx.simpleapp.user.service;

import com.aukustomx.simpleapp.common.model.ResponseVO;
import com.aukustomx.simpleapp.infra.exception.UserException;
import com.aukustomx.simpleapp.user.model.User;
import com.aukustomx.simpleapp.user.model.UserRequest;
import com.aukustomx.simpleapp.user.persistence.UserRepository;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.aukustomx.simpleapp.common.model.ResponseCode.*;

@Singleton
public class UserService {

    private static final List<User> users = new ArrayList<>();

    @Inject
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        users.add(new User(1, "Juan", "Juan@mail.com"));
        users.add(new User(2, "Pedro", "Pedro@mail.com"));
        users.add(new User(3, "María", "María@mail.com"));
        users.add(new User(4, "Jose", "Jose@mail.com"));
        users.add(new User(5, "Ana", "Ana@mail.com"));
    }

    /**
     * All users
     *
     * @return
     */
    public ResponseVO<List<User>> users() {
        return ResponseVO.successful(Collections.unmodifiableList(users));
    }

    /**
     * An User by ID or USER_DOES_NOT_EXISTS
     *
     * @param id User's ID
     * @return Response with User information
     */
    public ResponseVO<Map<String, Object>> byId(int id) {
        User user = userRepository.byId(id);

        if (null == user) {
            throw new UserException(USER_DOES_NOT_EXISTS);
        }

        return ResponseVO.successful(user.asMap());

        /*
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .map(User::asMap)
                .map(ResponseVO::successful)
                .orElseThrow(() -> new UserException(USER_DOES_NOT_EXISTS));
                */
    }

    public ResponseVO<Map<String, Object>> add(UserRequest req) {
        //Validar que el email recibido no exista
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(req.getEmail()))) {
            throw new UserException(USER_ALREADY_EXISTS);
        }

        User user = new User(nextId(), req.getName(), req.getEmail());
        users.add(user);
        return ResponseVO.of(SUCCESSFUL_OPERATION, user.asMap());
    }

    private int nextId() {
        return users.size() + 1;
    }

    public ResponseVO delete(int id) {
        users.removeIf(u -> u.getId() == id);
        return ResponseVO.successful();
    }
}
