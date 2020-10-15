package hr.ferit.matijasokol.gallery.ui.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import hr.ferit.matijasokol.gallery.R
import hr.ferit.matijasokol.gallery.databinding.FragmentDetailsBinding
import hr.ferit.matijasokol.gallery.other.Constants.APP_NAME
import hr.ferit.matijasokol.gallery.other.Constants.STORAGE_PERMISSION_REQUEST_CODE
import hr.ferit.matijasokol.gallery.other.loadImageWithCallbacks
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val TAG = "[DEBUG] DetailsFragment"

    private val args by navArgs<DetailsFragmentArgs>()
    private val photo by lazy { args.photo }
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailsBinding.bind(view)

        with(binding) {
            ivImageDetails.loadImageWithCallbacks(photo.urls.regular, this@DetailsFragment, { onImageLoadFailed() }, { onImageResourceReady() })
            tvDescription.text = photo.description
            val uri = Uri.parse(photo.user.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            tvCreator.apply {
                text = getString(R.string.image_description, photo.user.name)
                setOnClickListener {
                    requireContext().startActivity(intent)
                }
                paint.isUnderlineText = true
            }
        }

        setListeners()
    }

    private fun onImageLoadFailed(): Boolean {
        binding.progressBar.isVisible = false
        return false
    }

    private fun onImageResourceReady(): Boolean {
        with(binding) {
            progressBar.isVisible = false
            tvCreator.isVisible = true
            tvDescription.isVisible = photo.description != null
        }
        return false
    }

    private fun setListeners() {
        binding.ivImageDetails.setOnLongClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askStoragePermission()
            } else {
                saveImageToGallery()
            }
            true
        }
    }

    fun saveImageToGallery() {
        val bmpDrawable = binding.ivImageDetails.drawable as BitmapDrawable
        val bitmap = bmpDrawable.bitmap

        try {
            val sdCard = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Environment.getExternalStorageDirectory()
            } else {
                requireContext().getExternalFilesDir(null)
            }

            sdCard?.let {
                val dir = File("${sdCard.absolutePath}/$APP_NAME")
                if (!dir.exists() && !dir.mkdirs()) {
                    Toast.makeText(requireContext(), getString(R.string.can_not_make_dir), Toast.LENGTH_SHORT).show()
                    return
                }

                val imageName = args.query.replace(" ", "_")
                val authorName = args.photo.user.name.replace(" ", "_")
                val fileName = "${imageName}_${authorName}_${System.currentTimeMillis()}.jpg"
                val outFile = File(dir, fileName)
                val fileOutputStream = FileOutputStream(outFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
                Toast.makeText(requireContext(), getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
                refreshGallery(outFile)
            }
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "saveImageToGallery: $e")
        } catch (e: IOException) {
            Log.e(TAG, "saveImageToGallery: $e")
        } catch (e: Exception) {
            Log.e(TAG, "saveImageToGallery: $e")
        }
    }

    private fun refreshGallery(file: File) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also {
                it.data = Uri.fromFile(file)
                requireContext().sendBroadcast(it)
            }
        } else {
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(file.toString()),
                arrayOf(file.name),
                null
            )
        }
    }

    private fun askStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}