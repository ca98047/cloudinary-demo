package com.musinsa.jinow
// Import the required packages

import com.cloudinary.Cloudinary
import com.cloudinary.Transformation
import com.cloudinary.utils.ObjectUtils
import io.github.cdimascio.dotenv.Dotenv
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL


val dotenv: Dotenv = Dotenv.load()
val cloudinary = Cloudinary(dotenv["CLOUDINARY_URL"])

fun main() {
    val creativeImage =
        "https://image.musinsa.com/images/plan_w_mobile_img/2024100412325900000010137.jpg"

    val upload = uploadToCloudinary(creativeImage)

    val publicId = upload["public_id"].toString()
    val resourceName = "${publicId}.${upload["format"]}"

    //transform
    val transformedImage = transformImage(resourceName)

    //download image
    downloadInLocal(transformedImage, resourceName)

    //destroy
    removeCloudinaryImage(publicId)
}

private fun uploadToCloudinary(
    cdnUrl: String,
): MutableMap<Any?, Any?> {
    val params1 = ObjectUtils.asMap(
        "use_filename", true,
        "unique_filename", false,
        "overwrite", true
    )

    //upload
    return cloudinary.uploader().upload(
        cdnUrl,
        params1
    ).also {
        println("Upload successful : $it")
    }
}

private fun transformImage(
    resourceName: String,
    crop: String = "fill",
    width: Int = 500,
    height: Int = 500,
    gravity: String = "auto"
): String =
    cloudinary.url()
        .transformation(
            Transformation<Transformation<*>>()
                .crop(crop)
                .width(width)
                .height(height)
                .gravity(gravity)
        ).generate(resourceName).also {
            println("Transformed image URL: $it")
        }

// Function to download an image from a URL and save it to a file
private fun downloadInLocal(imageUrl: String, localFileName: String) {
    try {
        val url = URL(imageUrl)
        val connection = url.openConnection()
        connection.connect()

        // Open input stream from the connection
        BufferedInputStream(connection.getInputStream()).use { inputStream ->
            // Open file output stream to save the image
            FileOutputStream(File("downloads/$localFileName")).use { outputStream ->
                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    outputStream.write(dataBuffer, 0, bytesRead)
                }
            }
        }
        println("Image downloaded successfully: $localFileName")
    } catch (e: Exception) {
        e.printStackTrace()
        println("Failed to download the image.")
    }
}

private fun removeCloudinaryImage(publicId: String) {
    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
}
