export function urlPatternCheck(url: string) {
    const urlRegex = /^(https?:\/\/)(www\.)?[a-zA-Z0-9@:%._\+~#?&//=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%._\+~#?&//=]*)$/;
    return urlRegex.test(url);
}

export function shuffleArray(array: string[]) {
    // Shuffle array
    const shuffled = array.sort(() => 0.5 - Math.random());

    // Get sub-array of first n elements after shuffled
    let selected = shuffled.slice(0, 3);
    return selected
}