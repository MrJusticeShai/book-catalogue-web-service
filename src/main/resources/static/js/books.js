function toggleView(view) {
    const books = document.querySelectorAll('.book-card');
    books.forEach(b => {
        if (view === 'list') {
            b.classList.remove('col-md-6');
            b.classList.add('col-12');
        } else {
            b.classList.remove('col-12');
            b.classList.add('col-md-6');
        }
    });
}
