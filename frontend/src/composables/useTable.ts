import { ref, reactive } from 'vue'

export function useTable(fetchFn: (params: any) => Promise<any>, defaultParams = {}) {
  const loading = ref(false)
  const data = ref<any[]>([])
  const total = ref(0)
  const page = ref(1)
  const pageSize = ref(20)
  const searchParams = reactive({ ...defaultParams })

  async function loadData() {
    loading.value = true
    try {
      const res = await fetchFn({ page: page.value, pageSize: pageSize.value, ...searchParams })
      if (res.data) {
        data.value = res.data.records || []
        total.value = res.data.total || 0
      }
    } finally {
      loading.value = false
    }
  }

  function handlePageChange(p: number) {
    page.value = p
    loadData()
  }

  function handleSizeChange(s: number) {
    pageSize.value = s
    page.value = 1
    loadData()
  }

  function handleSearch() {
    page.value = 1
    loadData()
  }

  function handleReset() {
    Object.keys(searchParams).forEach(k => {
      (searchParams as any)[k] = (defaultParams as any)[k] || undefined
    })
    handleSearch()
  }

  return {
    loading, data, total, page, pageSize, searchParams,
    loadData, handlePageChange, handleSizeChange, handleSearch, handleReset
  }
}
