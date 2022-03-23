import { TeamDTO } from "@/app/common/dtos/TeamDTO";
import settingService from "@/app/services/settingService";
import authService from "@/app/services/authService";
import React, { useEffect, useState } from "react";
import {
  Button,
  InputGroup,
  Table,
  Modal,
  FormControl,
  Form,
} from "react-bootstrap";
import { IconUserPlus, IconX, IconLoader } from "@tabler/icons";
import { LoggedInUserDto } from "@/app/common/dtos/LoggedInUserDto";
import { AxiosResponse } from "axios";
import moment from "moment";
import bannerNotificationService from "@/app/services/bannerNotificationService";

const MembersTab = () => {
  const [teamMembers, setTeamMembers] = useState<TeamDTO | undefined | null>();
  const [email, setEmail] = useState<string>("");
  const [isDisabled, setIsDisabled] = useState(true);
  const [user, setUser] = useState<LoggedInUserDto | null>();
  const [roles, setRoles] = useState<string[] | null | undefined>([]);
  const [updateRoleFlag, setUpdateRoleFlag] = useState<number | undefined>();
  useEffect(() => {
    if (user === undefined) {
      authService
        .whoAmI()
        .then((res: AxiosResponse<LoggedInUserDto>) => {
          setUser(res.data);
        })
        .catch(() => {
          setUser(null);
        });
    }
    settingService
      .teamMember()
      .then(({ data }) => {
        setTeamMembers(data as any);
      })
      .catch(() => {
        setTeamMembers(null);
      });
    settingService
      .roleList()
      .then(({ data }) => {
        setRoles(data as any);
      })
      .catch(() => {
        setRoles(null);
      });
  }, []);
  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const sendInvite = () => {
    settingService
      .inviteMember([{ email }])
      .then(() => {
        bannerNotificationService.success("Invitation sent successfully");
        const pendingInvitees = [
          ...(teamMembers?.pendingInvitees as any),
          { email },
        ];
        setTeamMembers({ ...teamMembers, pendingInvitees } as any);
        handleClose();
      })
      .catch((err: any) => {
        bannerNotificationService.error(err?.response?.data?.message);
      });
  };
  const resendInvitation = (email: string) => {
    settingService
      .resendInvitation([{ email }])
      .then(() => {
        bannerNotificationService.success("Resent invitation");
      })
      .catch((err) => {
        console.error(err.response);
        if (
          err &&
          err.response &&
          err.response.data &&
          err.response.data.message
        ) {
          bannerNotificationService.error(err.response.data.message);
        }
      });
  };
  const cancelInvitation = (email: string) => {
    settingService
      .cancelInvitation([email])
      .then(() => {
        const index = teamMembers?.pendingInvitees?.findIndex(
          (mem: any) => mem.email === email
        );
        index !== undefined &&
          index >= 0 &&
          teamMembers?.pendingInvitees?.splice(index, 1);
        const pendingInvitees = teamMembers?.pendingInvitees || [];
        setTeamMembers({ ...teamMembers, pendingInvitees } as any);
        bannerNotificationService.success("Cancelled invitation");
      })
      .catch((err: any) => {
        console.log(err);
        bannerNotificationService.error(err.message);
      });
  };
  const removeMember = (email: string) => {
    settingService
      .removeMember([email])
      .then(() => {
        bannerNotificationService.success("Removed member");
        const index = teamMembers?.activeMembers?.findIndex(
          (mem: any) => mem.email === email
        );
        index !== undefined &&
          index >= 0 &&
          teamMembers?.activeMembers?.splice(index, 1);
        const activeMembers = teamMembers?.activeMembers || [];
        setTeamMembers({ ...teamMembers, activeMembers } as any);
      })
      .catch((err) => {
        console.log(err);
        bannerNotificationService.error(err.message);
      });
  };

  const handleChange = (event: any) => {
    setEmail(event.target.value);
    if (validateEmail(event.target.value))
      setIsDisabled(false);
    else
      setIsDisabled(true);
  };

  const validateEmail = (email: string) => {
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
  }

  const onChangeRole = (event: any, email: string) => {
    let obj = teamMembers?.activeMembers?.find(
      (mem: any) => mem.email === email
    );
    if (obj) obj.role = event.target.value;
    const index = teamMembers?.activeMembers?.findIndex(
      (mem: any) => mem.email === email
    );
    setUpdateRoleFlag(index);
    index !== undefined &&
      index >= 0 &&
      teamMembers?.activeMembers?.splice(index, 1);
    index !== undefined &&
      index >= 0 &&
      teamMembers?.activeMembers?.splice(index, 0, obj as any);
    settingService
      .updateRole(event.target.value, email)
      .then(() => {
        bannerNotificationService.success("Role Updated Successfully");
        setUpdateRoleFlag(undefined);
      })
      .catch((err) => {
        console.log(err);
        bannerNotificationService.error(err.message);
      });
  };

  return (
    <div>
      <div className="d-flex justify-content-between">
        <h3 className="mb-1 mt-4 font-weight-bold">Members</h3>
        {user?.role === "ADMIN" && (
          <div className="mt-3 mb-2">
            <Button variant="outline-primary" size="sm"
              onClick={() => {
                setEmail("");
                handleShow()
              }}
              className="animate"
              >
              <span>
                <IconUserPlus size={14} className="sidebar-icon" />
                &nbsp; Invite team member
              </span>
            </Button>
          </div>
        )}
      </div>
      <Table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email Address</th>
            <th>Role</th>
            <th>Member Since</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {teamMembers !== undefined &&
            teamMembers?.activeMembers.map((fieldMapping, i) => (
              <tr key={i} className="animate">
                <td>
                  <div style={{ width: "115px" }}>{fieldMapping.name}</div>
                </td>
                <td>
                  <div style={{ width: "150px" }}>{fieldMapping.email}</div>
                </td>
                <td>
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <div style={{ width: "90px" }}>
                      {user?.role === "ADMIN" ? (
                        <Form.Select
                          size="sm"
                          disabled={updateRoleFlag === i}
                          value={fieldMapping.role}
                          onChange={(e: any) =>
                            onChangeRole(e, fieldMapping.email)
                          }
                        >
                          {roles?.map((role) => {
                            return <option value={role}>{role}</option>;
                          })}
                        </Form.Select>
                      ) : (
                        fieldMapping.role
                      )}
                    </div>
                    <div style={{ width: "14px" }}>
                      {updateRoleFlag === i && (
                        <IconLoader size={12} className="spinner-icon" />
                      )}
                    </div>
                  </div>
                </td>
                <td>
                  <div style={{ width: "150px" }}>
                    {moment(fieldMapping.createdTs).format(
                      "DD MMM YYYY HH:mm A"
                    )}
                  </div>
                </td>
                <td>
                  {fieldMapping.role &&
                    (fieldMapping.role as any) !== "ADMIN" &&
                    user?.role === "ADMIN" && (
                      <Button
                        size="sm"
                        variant="link"
                        className="btn-link-danger"
                        onClick={() => removeMember(fieldMapping.email)}
                      >
                        Revoke
                      </Button>
                    )}
                </td>
              </tr>
            ))}
        </tbody>
      </Table>

      {teamMembers && teamMembers!.pendingInvitees.length !== 0 && (
        <>
          <h3 className="mb-3 mt-4 font-weight-bold">
            {teamMembers!.pendingInvitees.length} pending invite
          </h3>
          <Table>
            <thead>
              <tr>
                <th>Email address</th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {teamMembers !== undefined &&
                teamMembers!.pendingInvitees.map((fieldMapping, i) => (
                  <tr key={i}>
                    <td>{fieldMapping.email}</td>
                    <td>
                      {user?.role === "ADMIN" && (
                        <Button
                          size="sm"
                          variant="link"
                          onClick={() => resendInvitation(fieldMapping.email)}
                        >
                          Resend invitation
                        </Button>
                      )}
                    </td>
                    <td>
                      {user?.role === "ADMIN" && (
                        <Button
                          size="sm"
                          variant="link"
                          className="btn-link-danger"
                          onClick={() => cancelInvitation(fieldMapping.email)}
                        >
                          Revoke
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
            </tbody>
          </Table>
        </>
      )}
      <Modal show={show} onHide={handleClose} centered>
        <Modal.Header closeButton>
          <Modal.Title>Invite team member</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <InputGroup className="mb-3">
            <FormControl
              type="email"
              placeholder="Email address"
              onChange={handleChange}
              required
            />
          </InputGroup>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-danger" onClick={handleClose}>
            Cancel
          </Button>
          <Button variant="primary" disabled={isDisabled} onClick={sendInvite}>
            Send invitation
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default MembersTab;
