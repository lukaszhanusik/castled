import React from "react";
import { useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";

function Verify() {
    const { query } = useRouter();
    return (
        <GuestLayout>
            <div className="container">
                <div className="row py-5">
                    <div className="col px-5 col-12 text-center">
                        <img src='/images/Castled-Logo.png' alt="Castled Logo" />
                        <img src='/images/checkMail.png' alt='Check Mail' />
                    </div>
                    <div className="p-0 col col-12 text-center">
                        {
                            query.email ?
                                <>
                                    <h3>We have sent you an email to <span className="text-danger"> {query.email} </span> <a href="/auth/signup" className="text-decoration-underline">Edit</a></h3>
                                    <h3>Click on the registration link in the email to create an account on Castled </h3>
                                </>
                                :
                                <>
                                    <h3>We have sent you a registration link to your email address.</h3>
                                    <h3>Please click on the link to create an account on Castled.</h3>
                                </>
                        }
                    </div>
                </div>
            </div>
        </GuestLayout>
    );
}

export default Verify;