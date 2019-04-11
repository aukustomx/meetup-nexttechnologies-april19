package com.aukustomx.simpleapp.user.service;

import com.aukustomx.simpleapp.common.model.ResponseVO;
import com.aukustomx.simpleapp.infra.exception.UserException;
import com.aukustomx.simpleapp.user.model.User;
import com.aukustomx.simpleapp.user.model.UserRequest;
import com.aukustomx.simpleapp.user.persistence.UserRepository;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

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

    public ResponseVO uploadPhoto(int id, MultipartFormDataInput inputFile) {

        //Validate user exists
        User user = userRepository.byId(id);
        if (null == user) {
            throw new UserException(USER_DOES_NOT_EXISTS);
        }

        // Extracting and saving file
        Map<String, List<InputPart>> uploadForm = inputFile.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("attachment");
        return saveFile(inputParts);
    }

    private ResponseVO saveFile(List<InputPart> inputParts) {
        for (InputPart inputPart : inputParts) {

            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                // convert the uploaded file to inputstream
                byte[] bytes = inputStreamToByteArray(inputStream);

                String path = System.getProperty("user.home") + File.separator + "uploads";
                File customDir = new File(path);

                if (!customDir.exists()) {
                    customDir.mkdir();
                }
                fileName = customDir.getCanonicalPath() + File.separator + fileName;
                writeFile(bytes, fileName);

                return ResponseVO.successful("Uploaded file name : " + fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseVO.of(FAILED_OPERATION, null);
    }

    /*Helper methods*/
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] headers = header.getFirst("Content-Disposition").split(";");
        return Stream.of(headers)
                .map(String::trim)
                .filter(s -> s.startsWith("filename"))
                .findFirst()
                .map(s -> s.split("="))
                .map(array -> array[1])
                .map(String::trim)
                .map(s -> s.replaceAll("\"", ""))
                .orElse("unknown");

/*
        for (String filename : headers) {

            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
 */
    }

    private void writeFile(byte[] content, String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists()) {
            boolean isCreated = file.createNewFile();
            if (isCreated) {
                System.out.println("Archivo creado correctamente: " + filename);
            }
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
        System.out.println("Written: " + filename);
    }

    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
