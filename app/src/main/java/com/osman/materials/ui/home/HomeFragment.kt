package com.osman.materials.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.osman.materials.R
import com.osman.materials.databinding.FragmentHomeBinding
import com.osman.materials.domain.BaseVS
import com.osman.materials.domain.interactor.download_material.DownloadMaterialsVS
import com.osman.materials.domain.interactor.get_materials.GetMaterialsVS
import com.osman.materials.domain.model.Material
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import permissions.dispatcher.*
import java.io.File
import javax.inject.Inject


@RuntimePermissions
class HomeFragment : DaggerFragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MaterialsViewModel by viewModels {
        viewModelFactory
    }

    private val compositeDisposable = CompositeDisposable()
    private val materialsPublisher = BehaviorSubject.create<MaterialsIntent>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var materialsAdapter: MaterialsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val viewModel = ViewModelProvider(this, factory)[MaterialsViewModel::class.java]
        compositeDisposable.add(
            viewModel.states().subscribe({ render(it) }, { render(BaseVS.Error(it)) })
        )
        viewModel.processIntents(materialsPublisher)

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun render(state: BaseVS) {
        when (state) {
            is BaseVS.Loading -> {
                if (state.type == -1) {
                    binding.swipeRefresh.isRefreshing = true
                } else {
                    materialsAdapter.setItemDownloadProgress(state.type, 0)
                }
            }
            is BaseVS.Error -> {
                if (state.type == -1) {
                    binding.swipeRefresh.isRefreshing = false
                    binding.errorMsgItem.text = state.message
                    binding.recycler.visibility = View.GONE
                    binding.errorMsgItem.visibility = View.VISIBLE
                    binding.retryBtn.visibility = View.VISIBLE
                } else {
                    materialsAdapter.setItemDownloadProgress(state.type, -1)
                }
            }
            is BaseVS.Empty -> {
                binding.swipeRefresh.isRefreshing = false
                binding.errorMsgItem.setText(R.string.no_data)
                binding.recycler.visibility = View.GONE
                binding.errorMsgItem.visibility = View.VISIBLE
                binding.retryBtn.visibility = View.GONE
            }
            is GetMaterialsVS -> {
                binding.swipeRefresh.isRefreshing = false
                materialsAdapter.values = state.materialEntities.toMutableList()
                binding.recycler.visibility = View.VISIBLE
                binding.errorMsgItem.visibility = View.GONE
                binding.retryBtn.visibility = View.GONE
            }
            is DownloadMaterialsVS -> {
                materialsAdapter.setItemDownloadProgress(
                    state.position,
                    state.materialFile.progress,
                    state.materialFile.file.absolutePath
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.recycler) { view, insets ->
            binding.bottomBar.layoutParams = binding.bottomBar.layoutParams.apply {
                height = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            }
            view.updatePadding(bottom = view.paddingBottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
            insets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener(this)
        materialsAdapter = MaterialsAdapter(::onItemDownloadClick, ::onMaterialClick)
        binding.recycler.run {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
            setHasFixedSize(true)
            adapter = materialsAdapter
        }

        binding.retryBtn.setOnClickListener { onRefresh() }
        onRefresh()
    }

    private fun onMaterialClick(material: Material) {
        if (material.type == Material.Type.PDF) {
            if (material.isLocale) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val file: File = File(material.url)
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().packageName.toString() + ".provider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(intent)
                } else {
                    var intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(material.url), "application/pdf")
                    intent = Intent.createChooser(intent, "Open File")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://docs.google.com/gview?embedded=true&url=${material.url}".toUri()
                )
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
            }
        }
    }

    override fun onRefresh() {
        materialsPublisher.onNext(MaterialsIntent.GetMaterials)
    }

    private fun onItemDownloadClick(position: Int, url: String) {
        downloadItemWithPermissionCheck(position, url)
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun downloadItem(position: Int, url: String) {
        materialsPublisher.onNext(MaterialsIntent.DownloadMaterial(position, url))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        Toast.makeText(
            requireContext(),
            R.string.permission_denied,
            Toast.LENGTH_SHORT
        ).show()
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForPermission(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_rationale, request)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionNeverAskAgain() {
        Toast.makeText(
            requireContext(),
            R.string.permission_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.button_allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.button_deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }
}