export function openAndInject(sessionUrl: string) {
  const url = 'http://localhost:4200/';
  const newTab = window.open(sessionUrl, '_blank');

  if (newTab) {
    const doc = newTab?.document;
    const script = document.createElement('script')

    function injectFunc() {
      alert('Injected');
    }
    script.innerHTML = 'window.onload = ' + injectFunc.toString() + ';';
    doc.body.appendChild(script)
  } else {
    console.error('Failed to open the new tab. Please check your browser settings.');
  }
}