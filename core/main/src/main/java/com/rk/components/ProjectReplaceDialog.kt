package com.rk.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rk.file.FileObject
import com.rk.searchreplace.ProjectReplaceManager
import com.rk.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProjectReplaceDialog(
    projectRoot: FileObject,
    onFinish: () -> Unit,
) {
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    XedDialog(onDismissRequest = onFinish, modifier = Modifier.imePadding()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Replace in Project")

            OutlinedTextField(
                value = findText,
                onValueChange = { findText = it },
                label = { Text("Find") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = replaceText,
                onValueChange = { replaceText = it },
                label = { Text("Replace with") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onFinish) { Text("Cancel") }

                Button(
                    onClick = {
                        if (findText.isBlank()) {
                            toast("Find text can't be empty")
                            return@Button
                        }

                        // Fire-and-forget; result is surfaced via toast.
                        scope.launch(Dispatchers.IO) {
                            ProjectReplaceManager.replaceAllInProject(
                                projectRoot = projectRoot,
                                query = findText,
                                replacement = replaceText,
                            )
                        }
                        onFinish()
                    },
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("Replace All")
                }
            }
        }
    }
}
