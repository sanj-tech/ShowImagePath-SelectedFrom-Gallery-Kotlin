package com.example.imagecapture

import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.Images
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    val REQUEST_IMAGE_GALLERY = 11
    lateinit var uri: Uri
    val REQUEST_IMAGE_CAMERA = 12


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        imageView = findViewById(R.id.imagee)
        imageView.setOnClickListener({
            val builder = AlertDialog.Builder(this)
            builder.setTitle("select Image")
            builder.setMessage("Choose your option")
            builder.setPositiveButton("Gallery") { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"

                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)

            }
            builder.setNegativeButton("Camera") { dialog, which ->
                dialog.dismiss()
                Intent(ACTION_IMAGE_CAPTURE).also { takePicture ->
                    takePicture.resolveActivity(packageManager)?.also {
                        val permission = ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.CAMERA
                        )
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(android.Manifest.permission.CAMERA),
                                1
                            )


                        } else {
                            startActivityForResult(takePicture, REQUEST_IMAGE_CAMERA)
                        }
                    }
                }

            }
            var dialog: AlertDialog = builder.create()
            dialog.show()


        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageView.setImageURI(data.data)

            uri = data.data!!
            var res = getRealPathFromURI(uri)

            Toast.makeText(this, "get path $res", Toast.LENGTH_LONG).show()

        } else
            if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
                imageView.setImageBitmap(data.extras?.get("data") as Bitmap)


            } else {
                Toast.makeText(this, "data not found", Toast.LENGTH_LONG).show()
            }


    }


    private fun getRealPathFromURI(uri: Uri): String {

        val projection = arrayOf(Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)


    }


}
