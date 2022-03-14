import Layout from "@/app/components/layout/Layout";
import Link from "next/link";
import { IconChevronRight } from "@tabler/icons";
import { Badge } from "react-bootstrap";
import { useSession } from "@/app/common/context/sessionContext";

const Welcome = () => {
  const { isOss } = useSession();
  return (
    <Layout title="Welcome" subTitle={undefined} hideHeader>
      <div className="welcome-wrapper">
        <p className="mb-0">Welcome to Castled!</p>
        <h2 className="mb-4">Get started with your first pipeline.</h2>
        {!isOss && (
          <div className="card mb-4 p-3">
            <Link href="/pipelines/create?demo=1">
              <a className="row">
                <div className="col-3">
                  <img
                    src="/images/demo-warehouse.png"
                    className="card-img-top"
                  />
                </div>
                <div className="col-8">
                  <strong>Don’t have warehouse credentials?</strong>
                  <h3>Create a pipeline with demo warehouse</h3>
                  <p>
                    Quickly test a pipeline sync even before you get the
                    warehouse credentials from your DevOps team.
                  </p>
                </div>
                <div className="col-1">
                  <IconChevronRight size={24} className="text-muted" />
                </div>
              </a>
            </Link>
          </div>
        )}
        <div className="card mb-4 p-3">
          <Link href="/pipelines/create?wizardStep=source:selectType">
            <a className="row">
              <div className="col-3">
                <img src="/images/warehouses.png" className="card-img-top" />
              </div>
              <div className="col-8">
                <strong>Have the credentials of your warehouse?</strong>
                <h3>Create a pipeline with your own warehouse</h3>
                <p>
                  Our exhaustive documentation will help you set up your
                  pipeline in real quick time.
                </p>
              </div>
              <div className="col-1">
                <IconChevronRight size={24} className="text-muted" />
              </div>
            </a>
          </Link>
        </div>
      </div>
    </Layout>
  );
};

export default Welcome;
