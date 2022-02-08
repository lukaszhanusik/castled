import React from "react";
import {
  IconApps,
  IconGitCompare,
  IconDatabase,
  IconSettings,
} from "@tabler/icons";
import Link from "next/link";
import cn from "classnames";
import { useRouter } from "next/router";
import DropdownCaretDown from "@/app/components/bootstrap/DropdownCaretDown";
import { OverlayTrigger, Tooltip, Dropdown } from "react-bootstrap";
import { useSession } from "@/app/common/context/sessionContext";

interface LeftSidebarProps { }

const sidebarLinks = [
  {
    icon: IconGitCompare,
    title: "Pipelines",
    href: "/pipelines",
    enabledInOss: true,
  },
  {
    icon: IconDatabase,
    title: "Warehouses",
    href: "/warehouses",
    enabledInOss: true,
  },
  {
    icon: IconApps,
    title: "Apps",
    href: "/apps",
    enabledInOss: true,
  },
  {
    icon: IconSettings,
    title: "Settings",
    href: "/settings",
    enabledInOss: false,
  },
];
const LeftSidebar = (props: LeftSidebarProps) => {
  const { user, isOss } = useSession();
  const router = useRouter();
  return (
    <aside className="col d-md-block sidebar collapse">
      <div className="d-flex flex-column flex-shrink-0">
        <a href="/" className="d-block logo" title="">
          <img
            src="/images/favicon/android-chrome-512x512.png"
            width={40}
            className="rounded-circle"
          />
          {/* Castled */}
        </a>
        <ul className="nav nav-pills nav-flush flex-column mb-auto text-center">
          {sidebarLinks
            .filter((li) => !isOss || li.enabledInOss == true)
            .map((li, i) => {
              const Icon = li.icon;

              return (
                <OverlayTrigger
                  placement="right"
                  key={`sidebar-${i}`}
                  overlay={<Tooltip id={`sidebar-${i}`}>{li.title}</Tooltip>}
                >
                  <li
                    className="nav-item"
                    key={`sidebar-${i}`}
                    data-bs-toggle="tooltip"
                    data-bs-placement="right"
                    title="Tooltip on right"
                  >
                    <Link href={li.href}>
                      <a
                        className={cn("nav-link py-3", {
                          active: router.pathname.indexOf(li.href) === 0,
                        })}
                        aria-current="page"
                        title=""
                        data-bs-toggle="tooltip"
                        data-bs-placement="right"
                        data-bs-original-title="Home"
                      >
                        <div>
                          <Icon size={18} className="sidebar-icon" />
                        </div>
                        <div>{li.title}</div>
                      </a>
                    </Link>
                  </li>
                </OverlayTrigger>
              );
            })}
          <li className="position-absolute bottom-0 text-center">
            {user && !isOss && (
              <Dropdown align="end">
                <Dropdown.Toggle
                  id="logout-dropdown"
                  className="ms-2 border-0 shadow-none"
                >
                  <img
                    src={`https://ui-avatars.com/api/?name=${user.name}`}
                    alt={user.name}
                    height={44}
                    className="rounded-circle"
                  />
                </Dropdown.Toggle>
                <Dropdown.Menu align="end">
                  <Dropdown.Item href="/auth/logout" className="text-primary">Logout</Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            )}
          </li>
        </ul>
      </div>
    </aside>
  );
};
export default LeftSidebar;
