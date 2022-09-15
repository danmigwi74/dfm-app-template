package com.ekenya.rnd.wallet.ui.airtime

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekenya.rnd.wallet.R
import com.ekenya.rnd.wallet.data.ContactListWalletModel
import com.ekenya.rnd.wallet.databinding.FragmentBuyAirtimeHomeWalletBinding
import com.ekenya.rnd.wallet.ui.adapters.AddBeneficiaryWalletAdapter
import com.ekenya.rnd.wallet.ui.adapters.ContactListWalletAdapter
import com.ekenya.rnd.wallet.ui.adapters.FrequentBuyAirTimeWalletAdapter
import com.ekenya.rnd.wallet.ui.adapters.listeners.OnContactItemClickedListener
import com.ekenya.rnd.wallet.ui.home.mainFragments.MainBuyAirTimeWalletFragment
import com.ekenya.rnd.wallet.utils.*

class HomeBuyAirTimeFragment : BaseWalletFragment<FragmentBuyAirtimeHomeWalletBinding>(
    FragmentBuyAirtimeHomeWalletBinding::inflate
), LoaderManager.LoaderCallbacks<Cursor> {
    private var contactListWalletModel: ArrayList<ContactListWalletModel> = ArrayList()

    //init select contact adapter
    private val mAdapter: ContactListWalletAdapter by lazy {
        ContactListWalletAdapter(onContactItemClickedListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkContactsPermissions()
        setUpUi()

    }

    private fun setUpUi() {

        binding.clToolBar.toolbar.setBackButton(R.string.buy_airtime_wallet, requireActivity())

        binding.iVMyself.text = binding.tVTitleMyself.text.toString().getFirstLetter()
        binding.iVOthers.text = binding.tVTitleOthers.text.toString().getFirstLetter()

        binding.cvMyself.setOnClickListener {
            findNavController().navigate(R.id.buyAirTimeWalletFragment)
        }
        binding.cvMyself.setOnClickListener {
            findNavController().navigate(R.id.buyAirTimeWalletFragment)
        }

        binding.fabDailPad.setOnClickListener {
            findNavController().navigate(R.id.buyAirTimeWalletFragment)
        }

    }

    companion object {
        private const val CONTACTS_LOADER_ID = 1

        fun newInstance() =
            MainBuyAirTimeWalletFragment()
    }


    //requests for permissions
    private fun requestContactsPermission() {
        if (shouldShowRequestPermissionRationale(
                Manifest.permission.READ_CONTACTS
            )
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                0
            )

        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                0
            )
        }
    }

    //set up contacts recyclerview
    private fun initContactsAdapter() {
        contactListWalletModel.clear()

        contactListWalletModel.add(
            0,
            ContactListWalletModel(
                titleContactName = "Add Beneficiary",
                phoneContact = "",
                initialsContact = "+"
            )
        )
        contactListWalletModel.add(
            1,
            ContactListWalletModel(
                titleContactName = "Myself",
                phoneContact = "+25412345678",
                initialsContact = "M"
            ),
        )
        contactListWalletModel.addAll(ContactListWalletModel.getCAddBeneficiaryModel())

        binding.rvAddBeneficiary.apply {
            adapter = AddBeneficiaryWalletAdapter(
                contactListWalletModel,
                onContactItemClickedListener
            )
            layoutManager =
                object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                    override fun canScrollVertically(): Boolean = false
                }
            //LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvFrequent.apply {
            adapter = FrequentBuyAirTimeWalletAdapter(
                ContactListWalletModel.getContactModel(),
                onContactItemClickedListener
            )
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
        }
        binding.rvAllContacts.apply {
            adapter = mAdapter
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean = false
            }
            //LinearLayoutManager(requireContext())
        }
        //start loading contacts
        LoaderManager.getInstance(requireActivity())
            .initLoader(CONTACTS_LOADER_ID, null, this)
    }

    private val onContactItemClickedListener = object : OnContactItemClickedListener {
        override fun onClickedItem(view: View, model: ContactListWalletModel) {
            when (view.id) {
                R.id.mcVRoot -> {
                    if (model.titleContactName == "Add Beneficiary") {
                        toasty("Add Beneficiary Coming Soon")
                    } else {
                        val bundle = Bundle()
                        bundle.putString(
                            getString(R.string.phone_number_wallet),
                            model.phoneContact
                        )
                        findNavController().navigate(R.id.buyAirTimeWalletFragment, bundle)
                        toasty("Buying For => ${model.phoneContact}")
                    }

                }
            }
        }

    }

    //check if has permission read contacts
    private fun checkContactsPermissions() {
        // read contacts if has permission
        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            // initialize the contacts recycler adapter
            initContactsAdapter()
        } else {
            // request  permissions if none
            requestContactsPermission()
        }
    }

    //check if a particular permission is granted
    private fun hasPermission(permission: String) =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    //callback after user interacts with permission dialog
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initContactsAdapter()
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
        return contactsLoader()
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
        mAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        mAdapter.swapCursor(null)
    }


}