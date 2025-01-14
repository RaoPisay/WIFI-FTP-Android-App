package noadd

import android.os.Environment
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission

object FTPServer {
    private val DEFAULT_DIR: String = Environment.getExternalStorageDirectory().path

    fun start() {
        try {
            // Create FTP server factory
            val serverFactory = FtpServerFactory()

            // Create listener factory and set port
            val listenerFactory = ListenerFactory()
            listenerFactory.port = 2221 // Use port 2221 to avoid permission issues on 21

            // Add listener to the server
            serverFactory.addListener("default", listenerFactory.createListener())

            // Create a user with no authentication (anonymous access)
            val user = BaseUser().apply {
                name = "anonymous"
                homeDirectory = DEFAULT_DIR
                //authorities = listOf(WritePermission()) // Allow file writing
                authorities = listOf(WritePermission(), TransferRatePermission(0, 0))

            }

            // Set up user manager without authentication
            val userManager = PropertiesUserManagerFactory().createUserManager()
            userManager.save(user)
            serverFactory.userManager = userManager

            // Start the FTP server
            val server = serverFactory.createServer()
            server.start()

            println("FTP Server started on ftp://${getLocalIpAddress()}:2221")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
