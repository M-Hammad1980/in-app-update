```
private var appUpdateManagerLib: InAppUpdateManager? = null
private var updateLauncher: ActivityResultLauncher<IntentSenderRequest> ?= null
```

oncreate 
```
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)


        // Initialize the InAppUpdateManager
        appUpdateManagerLib = InAppUpdateManager(this)

        // Register the activity result launcher
        updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                // Handle update failure if needed
                showToast(getString(R.string.update_failed))
            }
        }
        updateLauncher?.let {
            appUpdateManagerLib?.checkForUpdate(BuildConfig.VERSION_CODE,
                getRemoteConfig()[version_for_update_Key].toInt(),
                it, binding.root)
        }
    }
```

on stop
```
override fun onStop() {
        super.onStop()
        appUpdateManagerLib?.unregisterListener()
    }
```

on destroy
```
override fun onDestroy() {
        super.onDestroy()
        appUpdateManagerLib?.unregisterListener()
    }
```


onresume 
```
override fun onResume() {
        super.onResume()
        appUpdateManagerLib?.onResume()
    }
```
