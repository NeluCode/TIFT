package com.nelu.tift.config

object Scrapper {

    val myvidintenturlis = "https://www.tiktok.com/@foodoclocktv/video/7329928305682025770"

    //
    fun String.getVideoPasteFunc() = "javascript:(function() { " +
            "document.getElementById('url').value ='" + "https://www.tiktok.com/@villainmasud01/video/$this" + "';" +
            "document.getElementById('send').click();" +
            "})();"

    val checkProgress = """
            (function() {
                var divContent = document.querySelector('.progress-dl');
                if (divContent) {
                    return divContent.innerHTML;
                } else {
                    return null;
                }
            })();
        """.trimIndent()

    val getVideoInfo = """
            (function() {
                var divContent = document.querySelector('.savetik-downloader-middle.text-center');
                if (divContent) {
                    var titleElement = divContent.querySelector('h3');
                    var descriptionElement = divContent.querySelector('p span');
                    var imageElement = document.querySelector('.savetik-downloader-left img');
        
                    var content = {};
                    if (titleElement && descriptionElement && imageElement) {
                        content.title = titleElement.textContent.trim();
                        content.description = descriptionElement.textContent.trim();
                        content.image = imageElement.getAttribute('src');
                    }
                    return JSON.stringify(content);
                } else {
                    return null;
                }
            })();
        """.trimIndent()
}