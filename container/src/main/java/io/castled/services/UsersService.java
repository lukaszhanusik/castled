package io.castled.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.CastledStateStore;
import io.castled.ObjectRegistry;
import io.castled.daos.InstallationDAO;
import io.castled.daos.TeamsDAO;
import io.castled.daos.UsersDAO;
import io.castled.dtos.UserDTO;
import io.castled.events.CastledEventsClient;
import io.castled.events.NewInstallationEvent;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.models.users.Team;
import io.castled.models.users.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.UUID;

@Singleton
@Slf4j
public class UsersService {

    private final TeamsDAO teamsDAO;
    private final UsersDAO usersDAO;
    private final InstallationDAO installationDAO;

    @Inject
    public UsersService(Jdbi jdbi) {
        this.teamsDAO = jdbi.onDemand(TeamsDAO.class);
        this.usersDAO = jdbi.onDemand(UsersDAO.class);
        this.installationDAO = jdbi.onDemand(InstallationDAO.class);
    }

    public UserDTO toDTO(User user) {
        Team team = this.teamsDAO.getTeam(user.getTeamId());
        return new UserDTO(user.getFullName(), user.getEmail(),
                user.getId(), user.getCreatedTs(), team);
    }

    public void createTestTeamAndUser(String email) {
        User user = getUser();
        if (user != null) {
            throw new BadRequestException(String.format("User %s already exists", user.getEmail()));
        }
        this.usersDAO.createTeamAndUser("test", email, "Test", "User");
        createNewInstallationIfRequired(email);
    }

    private void createNewInstallationIfRequired(String email) {
        String installId = installationDAO.getInstallation();
        if (installId != null) {
            CastledStateStore.installId = installId;
            return;
        }
        installationDAO.createInstallation(email);
        CastledStateStore.installId = email;
        ObjectRegistry.getInstance(CastledEventsClient.class).publishCastledEvent(new NewInstallationEvent(email));
    }

    public User getUser() {
        List<User> users = this.usersDAO.getAllUsers();
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        if (users.size() > 1) {
            throw new CastledRuntimeException("Multiple users found in database");
        }
        return users.get(0);
    }

}
