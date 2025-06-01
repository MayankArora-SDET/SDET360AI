import $ from 'jquery';
export function insertCheckBoxes() {
    if (typeof window) {
        $('markdown *').filter(function (this: any) {
            return $(this).next('ul').length > 0;
        }).each(function (this: any) {
            if ($(this).find("input").length == 0) {
                const checkbox = $(`<input type="checkbox" class="testCaseCheckBox" value="${$(this).text() + $(this).next('ul').text()}"/>`);
                $(this).prepend(checkbox); // Add the checkbox before the <p> tag
            }
        });
    }

}

export function getSelectedTestCaseList() {
    let selectedTestCases: string[] = []
    if (typeof window) {

        $(".testCaseCheckBox").each(function (this: any) {

            if (this.checked) {
                let testCaseVal = ($(this).parent().text() + $(this).parent().next('ul').text())
                selectedTestCases.push(testCaseVal)

            }
        })
        return selectedTestCases
    }
    return selectedTestCases

}