stop_word_list_datasets = ['dengan', 'ia', 'bahwa', 'oleh', 'saya', 'merasa', 'apa', 'jangan',
                           'bagian', 'mengalami', 'gimana', 'dong', 'bagaimana', 'gejala', 'merasa', 'merada', 'disertai']

# Response Bot
def responseText(penyakit = "coba", pengobatan = "Coba", definisi = "-", jenis = "-", pencegahan = "-", catatan = '-'):
    response = "Selamat Siang\nDari gejala yang anda sampaikan " \
               "Kemungkinan terbesar yang anda alami adalah penyakit {0},\n" \
               "{0} merupakan jenis penyakit {1}" \
               "\n\n{2}\n\n" \
               "{3}\n\n" \
               "{4}\n\n" \
               "{5}\n\n" \
               "Semoga Informasi ini cukup membantu dan lekas sembuh untuk anda.\n" \
               "Sehat Selalu\n" \
               "Salam." \
        .format(penyakit, jenis, definisi, pengobatan, pencegahan, catatan)
    return response