import React from "react";
import { useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";

function Verify() {
  const router = useRouter();
  return (
    <GuestLayout>
      <div className="container">
        <div className="row py-5">
          <div className="col px-5 col-12 text-center">
            <img src="/images/Castled-Logo.png" alt="Castled Logo" />
            <img src="/images/checkMail.png" alt="Check Mail" />
          </div>
          <div className="p-0 col col-12 text-center">
            {router.query.email ? (
              <>
                <h3>
                  We have sent a registration link to{" "}
                  <span className="text-danger"> {router.query.email} </span>{" "}
                  <span className="text-decoration-underline text-primary" id="edit-email"
                    onClick={() => {
                      router.push(
                        {
                          pathname: '/auth/signup',
                          query: { email: router.query.email }
                        },
                        '/auth/signup',
                      );
                    }}>
                    Edit
                  </span>
                </h3>
                <h3>
                  Please click on the link to create an account on Castled.
                </h3>
              </>
            ) : (
              <>
                <h3>
                  We have sent you a registration link to your email address.
                </h3>
                <h3>
                  Please click on the link to create an account on Castled.
                </h3>
              </>
            )}
          </div>
        </div>
      </div>
    </GuestLayout>
  );
}

export default Verify;
