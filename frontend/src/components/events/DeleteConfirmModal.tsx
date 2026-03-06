export default function DeleteConfirmModal({
  onConfirm,
  onCancel,
}: {
  onConfirm: () => void;
  onCancel: () => void;
}) {
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full mx-4">
        <h2 className="text-xl text-[#1F2937] mb-2">Delete Event</h2>
        <p className="text-[#6B7280] mb-6">
          Are you sure you want to delete this event? This action cannot be
          undone.
        </p>
        <div className="flex gap-3 justify-end">
          <button
            onClick={onCancel}
            className="px-5 py-2 bg-gray-100 text-[#1F2937] rounded-full hover:bg-gray-200 transition-all"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className="px-5 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-all"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}
