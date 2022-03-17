import { useSession } from "@/app/common/context/sessionContext";
import { Nav } from "react-bootstrap";
import cn from "classnames";
import router, { useRouter } from "next/router";
import { DebugPipelineWizContext } from "@/app/components/pipeline/DebugPipelineWizContext";
import WizardStepsLayout from "@/app/components/layout/WizardStepsLayout";
import React, { useState } from "react";
import { WizardSteps } from "@/app/common/dtos/internal/WizardSteps";
import { StringAnyMap } from "@/app/common/utils/types";
import buttonHandler from "@/app/common/utils/buttonHandler";
import { IconPlus } from "@tabler/icons";
export interface HeaderProps {
  title: string | JSX.Element;
  subTitle: string | JSX.Element | undefined;
  pageTitle?: string;
  centerTitle?: boolean;
  navLinks?: {
    href: string;
    title: string;
  }[];
  rightBtn?: {
    id: string;
    href?: string;
    title: string;
    onClick?: any;
    dataLayer?: StringAnyMap;
    isLoading?: boolean;
  };
  steps?: WizardSteps;
  stepGroups?: WizardSteps;
}

const Header = ({
  title,
  subTitle,
  centerTitle,
  navLinks,
  rightBtn,
  steps,
  stepGroups,
}: HeaderProps) => {
  const router = useRouter();
  const { isOss } = useSession();

  return (
    <>
      <header>
        <WizardStepsLayout
          title={title}
          steps={steps}
          stepGroups={stepGroups}
        />
        <div className="container-fluid container-main py-4">
          {title && (
            <div className="d-flex">
              <div className="flex-grow-1">
                <h1
                  className={cn("title-fs", {
                    "text-center": centerTitle,
                  })}
                >
                  {title}
                </h1>
              </div>
              <div className="">
                {rightBtn && (
                  <button
                    onClick={buttonHandler(
                      isOss,
                      { id: rightBtn.id },
                      rightBtn.href
                        ? () => {
                            if (process.browser) {
                              router.push(rightBtn.href!).then();
                            }
                          }
                        : rightBtn.onClick
                    )}
                    className="btn btn-primary btn-shadow float-end"
                  >
                    {(rightBtn.title === "Create Pipeline" ||
                      rightBtn.title === "Add Warehouse" ||
                      rightBtn.title === "Create Model" ||
                      rightBtn.title === "Add App") && (
                      <IconPlus size={16} className="me-1" />
                    )}
                    {rightBtn.title}
                  </button>
                )}
              </div>
            </div>
          )}
          {subTitle && (
            <p className="text-center text-muted px-5">{subTitle}</p>
          )}
          {navLinks?.length && (
            <Nav variant="tabs" defaultActiveKey={navLinks[0].href}>
              {navLinks.map((navLink, i) => (
                <Nav.Item key={i}>
                  <Nav.Link
                    href={navLink.href}
                    className={cn("nav-link", {
                      active: router.pathname === navLink.href,
                    })}
                  >
                    {navLink.title}
                  </Nav.Link>
                </Nav.Item>
              ))}
            </Nav>
          )}
        </div>
      </header>
      <DebugPipelineWizContext />
    </>
  );
};
export default Header;
