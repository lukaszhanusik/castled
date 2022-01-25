import React, { useEffect } from "react";
import { Form, Formik } from "formik";
import InputField from "@/app/components/forminputs/InputField";
import { useSession } from "@/app/common/context/sessionContext";
import { NextRouter, useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import * as Yup from "yup";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { GetServerSidePropsContext } from "next";
import axios, { AxiosResponse } from "axios";
import { Object } from "lodash";

export async function getServerSideProps(context: GetServerSidePropsContext) {
  return {
    props: {
      appBaseUrl: process.env.APP_BASE_URL
    }
  };
}

interface serverSideProps {
  appBaseUrl: string;
  apiBase: string;
}

interface userDetail{
  email: string;
}

function SignUp(props: serverSideProps) {
  const { setUser } = useSession();
  const router = useRouter();
  const formSchema = Yup.object().shape({
    email: Yup.string().email('Invalid email').required('Required'),
  });
  useEffect(() => {
    if (!router.isReady) return;

    if (router.query.failure_message) {
      bannerNotificationService.error(router.query.failure_message);
    }
  }, [router.isReady]);

  const handleSubmit = (values: userDetail) => {
    axios.post('https://app.castled.io/backend/v1/users/signup', {
      email: values.email
    })
    .then(res => {
      console.log(res);
      // router.push("/auth/verify");
    })
    .catch(err => console.log(err.message));
  }
  return (
    <GuestLayout>
      <div className="container">
        <div className="row py-5">
          <div className="col">
            <img src='/images/Castled-Logo.png' alt="Castled Logo" className="my-3" />
            <h2 className="mb-3">Create your Castled Account</h2>
            <Formik
              initialValues={{
                email: ""
              }}
              validationSchema={formSchema}
              onSubmit={(values) => handleSubmit(values)}
            >
              {({ values, setFieldValue, setFieldTouched }) => (
                <Form>
                  <InputField
                    type="email"
                    name="email"
                    title="Email"
                    placeholder="Enter email"
                  />
                  <ButtonSubmit className="form-control btn-lg" >
                    Sign Up
                  </ButtonSubmit>
                  <h4 className="mt-3"><a href="/auth/login">Login to existing account</a></h4>
                </Form>
              )}
            </Formik>
          </div>
        </div>
      </div>
    </GuestLayout>
  );
}

export default SignUp;