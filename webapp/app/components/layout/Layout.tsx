import Header, { HeaderProps } from "@/app/components/layout/Header";
import React from "react";
import LeftSidebar from "@/app/components/layout/LeftSidebar";
import HeadCommon from "@/app/components/layout/HeadCommon";
import { WizardSteps } from "@/app/common/dtos/internal/WizardSteps";

interface LayoutProps extends HeaderProps {
  children: React.ReactNode;
  steps?: WizardSteps;
  stepGroups?: WizardSteps;
  rightHelp?: React.ReactNode;
  hideHeader?: boolean;
}

const Layout = ({
  title,
  subTitle,
  centerTitle,
  pageTitle,
  navLinks,
  rightBtn,
  children,
  steps,
  stepGroups,
  rightHelp,
  hideHeader,
}: LayoutProps) => {
  return (
    <div className="layout-holder">
      <HeadCommon title={typeof title === "string" ? title : pageTitle || ""} />
      <LeftSidebar />
      <main>
        {!hideHeader && (
          <Header
            title={title}
            subTitle={subTitle}
            centerTitle={centerTitle}
            navLinks={navLinks}
            rightBtn={rightBtn}
            steps={steps}
            stepGroups={stepGroups}
          />
        )}
        {renderChildren(children, rightHelp)}
      </main>
    </div>
  );
};

const renderChildren = (
  children: React.ReactNode,
  rightHelp: React.ReactNode
) => {
  if (rightHelp) {
    return (
      <div className="container-fluid container-main-right-help row">
        <div className="col-6">{children}</div>
        <div className="col-6">{rightHelp}</div>
      </div>
    );
  } else {
    return <div className="container-fluid container-main">{children}</div>;
  }
};

export default Layout;
