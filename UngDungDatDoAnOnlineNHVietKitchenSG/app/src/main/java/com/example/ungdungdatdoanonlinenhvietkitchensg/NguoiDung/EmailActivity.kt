package com.example.ungdungdatdoanonlinenhvietkitchensg.NguoiDung

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.ungdungdatdoanonlinenhvietkitchensg.R
import com.example.ungdungdatdoanonlinenhvietkitchensg.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.*

class EmailActivity : ComponentActivity() {
    private lateinit var etEmail: EditText
    private lateinit var btnSend: AppCompatButton
    private lateinit var backButtonh: ImageButton
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)



        database = AppDatabase.getDatabase(this) // Khởi tạo AppDatabase
        etEmail = findViewById(R.id.edtnhapemail)
        btnSend = findViewById(R.id.btnSendEmail)
        backButtonh = findViewById(R.id.backButtonh) // Khởi tạo backButtonh

        backButtonh.setOnClickListener {
            finish()
        }

        val chuDe = intent.getStringExtra("title") ?: ""
        val noiDung = intent.getStringExtra("content") ?: ""
        val hinhAnhUri = intent.getStringExtra("imageUri")
        val txtUri = intent.getStringExtra("txtUri")
        val excelUri = intent.getStringExtra("excelUri")

        Log.d("EmailActivity", "Received: ChuDe=$chuDe, NoiDung=$noiDung, HinhAnhUri=$hinhAnhUri, TxtUri=$txtUri, ExcelUri=$excelUri")

        etEmail.setText("nhahangvietkitchensaigon@gmail.com")

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendEmail(email, chuDe, noiDung, hinhAnhUri, txtUri, excelUri)
        }
    }

    private fun getRealPathFromURI(uriString: String?): String? {
        uriString ?: return null
        val uri = Uri.parse(uriString)
        var result: String? = null

        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0 && cursor.moveToFirst()) {
                    val fileName = cursor.getString(columnIndex)
                    val file = File(cacheDir, fileName)
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    result = file.absolutePath
                }
            }
        } else if (uri.scheme == "file") {
            result = uri.path
        }
        return result
    }

    private fun sendEmail(email: String, chuDe: String, noiDung: String,
                          hinhAnhUri: String?, txtUri: String?, excelUri: String?) {
        val senderEmail = "ducnguyen212004@gmail.com"
        val senderPassword = "ddng ybip pmrt eqzy"

        /*val senderEmail = "2221004238@sv.ufm.edu.vn"
        val senderPassword = "vqps lrtk wlrl fxfw"*/

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    subject = "Liên hệ: $chuDe"

                    val messageBodyPart = MimeBodyPart()
                    messageBodyPart.setText(noiDung)

                    val multipart = MimeMultipart()
                    multipart.addBodyPart(messageBodyPart)

                    listOfNotNull(hinhAnhUri, txtUri, excelUri).forEach { uri ->
                        val filePath = getRealPathFromURI(uri)
                        if (!filePath.isNullOrEmpty()) {
                            val file = File(filePath)
                            if (file.exists()) {
                                val attachmentPart = MimeBodyPart()
                                val source = FileDataSource(file)
                                attachmentPart.dataHandler = DataHandler(source)
                                attachmentPart.fileName = file.name
                                multipart.addBodyPart(attachmentPart)
                            } else {
                                Log.e("EmailAttachment", "File không tồn tại: $filePath")
                            }
                        }
                    }
                    setContent(multipart)
                }
                Transport.send(message)
                runOnUiThread {
                    Toast.makeText(applicationContext, "Email đã gửi thành công", Toast.LENGTH_LONG).show()
                }
            } catch (e: MessagingException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}