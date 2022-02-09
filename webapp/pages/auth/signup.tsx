import React, { useEffect } from "react";
import { Form, Formik } from "formik";
import InputField from "@/app/components/forminputs/InputField";
import { useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import * as Yup from "yup";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { GetServerSidePropsContext } from "next";

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

interface userDetail {
  email: string | string[];
}

function SignUp(props: serverSideProps) {
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
    fetch('/v1/users/signup', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: values.email })
    })
      .then(res => {
        router.push(
          {
            pathname: '/auth/verify-email',
            query: { email: values.email }
          },
          '/auth/verify-email',
        );
      })
      .catch((err) => bannerNotificationService.error(err.message));
  }

  return (
    <GuestLayout>
      <div className="container">
        <div className="row py-2">
          <div className="col">
            <img src='/images/Castled-Logo.png' alt="Castled Logo" className="my-3" />
            <h2 className="mb-3">Create your Castled Account</h2>
            <Formik
              initialValues={
                router.query.email ?
                  {
                    email: router.query.email,
                  }
                  : {
                    email: "",
                  }
              }
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