package com.a2zdukhana.store

import android.content.Context


//Class is extending AsyncTask because this class is going to perform a networking operation
@Suppress("DEPRECATION")
class SendMail
    (

    private val context: Context,
    private val email: String,
    private val subject: String,
    private val message: String
) {
    //AsyncTask<Void?, Void?, Void?>()

    /*private var session: Session? = null

    //Progressdialog to show while sending email
    private var progressDialog: ProgressDialog? = null
    override fun onPreExecute() {
        super.onPreExecute()
        //Showing progress dialog while sending email
        progressDialog =
            ProgressDialog.show(context, "Sending message", "Please wait...", false, false)
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        //Dismissing the progress dialog
        progressDialog!!.dismiss()
        //Showing a success message
    }

     override fun doInBackground(vararg p0: Void?): Void? {
        //Creating properties
        val props = Properties()

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"

        //Creating a new session
        session = Session.getDefaultInstance(props,
            object : Authenticator() {
                //Authenticating the password
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("vineela.ambati7171@gmail.com", "@VINNI9849")
                }
            })
        try {
            //Creating MimeMessage object
            val mm = MimeMessage(session)

            //Setting sender address
            mm.setFrom(InternetAddress("vineela.ambati7171@gmail.com"))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            //Adding subject
            mm.subject = subject
            //Adding message
            mm.setText(message)

            //Sending email
            Transport.send(mm)
            Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show()

        } catch (e: MessagingException) {
            Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()
        }
        return null
    }*/



}