import { ref, reactive } from 'vue'
import type { FormInstance } from 'element-plus'

export function useForm(submitFn: (data: any) => Promise<void>, defaultData = {}) {
  const visible = ref(false)
  const loading = ref(false)
  const isEdit = ref(false)
  const formRef = ref<FormInstance>()
  const formData = reactive({ ...defaultData })

  function open(edit = false, row?: any) {
    isEdit.value = edit
    Object.keys(defaultData).forEach(k => {
      if (row && row[k] !== undefined) {
        (formData as any)[k] = row[k]
      } else {
        (formData as any)[k] = (defaultData as any)[k]
      }
    })
    visible.value = true
  }

  function close() {
    visible.value = false
    formRef.value?.resetFields()
  }

  async function submit() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return

    loading.value = true
    try {
      await submitFn({ ...formData, isEdit: isEdit.value })
      close()
    } finally {
      loading.value = false
    }
  }

  return { visible, loading, isEdit, formRef, formData, open, close, submit }
}
